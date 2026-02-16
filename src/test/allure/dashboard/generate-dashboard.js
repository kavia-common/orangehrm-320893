#!/usr/bin/env node
/**
 * Centralized Allure dashboard generator (Java JUnit + C# NUnit).
 *
 * What it does:
 *  1) Reads Allure raw results from:
 *      - Java:  src/test/selenium-java-junit/allure-results
 *      - C#:    src/test/selenium-csharp-nunit/OrangeHrm.SeleniumNUnit/bin/Release/allure-results
 *  2) Merges them into: src/test/allure/dashboard/out/combined-allure-results
 *     - Adds/overrides environment.properties to preserve per-source metadata
 *     - Copies all *-result.json, *-container.json, attachments, etc.
 *     - Avoids filename collisions by prefixing file names with "java-" / "csharp-"
 *  3) Runs Allure CLI to generate ONE unified HTML report at:
 *      src/test/allure/dashboard/out/allure-report
 *  4) Emits easy-to-read metrics:
 *      - JSON:  src/test/allure/dashboard/out/metrics/summary.json
 *      - HTML:  src/test/allure/dashboard/out/dashboard.html
 *
 * Requirements:
 *  - Node.js >= 16
 *  - Allure CLI available on PATH (see src/test/allure/README.md)
 */

const fs = require("fs");
const path = require("path");
const { spawnSync } = require("child_process");

const REPO_ROOT = path.resolve(__dirname, "../../../..");

const JAVA_RESULTS_DIR = path.join(
  REPO_ROOT,
  "src/test/selenium-java-junit/allure-results",
);

const CSHARP_RESULTS_DIR = path.join(
  REPO_ROOT,
  "src/test/selenium-csharp-nunit/OrangeHrm.SeleniumNUnit/bin/Release/allure-results",
);

const OUT_DIR = path.join(REPO_ROOT, "src/test/allure/dashboard/out");
const COMBINED_RESULTS_DIR = path.join(OUT_DIR, "combined-allure-results");
const ALLURE_REPORT_DIR = path.join(OUT_DIR, "allure-report");
const METRICS_DIR = path.join(OUT_DIR, "metrics");

function ensureDir(dirPath) {
  fs.mkdirSync(dirPath, { recursive: true });
}

function rmDirIfExists(dirPath) {
  if (fs.existsSync(dirPath)) {
    fs.rmSync(dirPath, { recursive: true, force: true });
  }
}

function isDirectory(p) {
  try {
    return fs.statSync(p).isDirectory();
  } catch {
    return false;
  }
}

function listFilesFlat(dirPath) {
  if (!isDirectory(dirPath)) return [];
  return fs
    .readdirSync(dirPath)
    .map((name) => path.join(dirPath, name))
    .filter((p) => fs.statSync(p).isFile());
}

function copyFileSafe(srcFile, dstFile) {
  ensureDir(path.dirname(dstFile));
  fs.copyFileSync(srcFile, dstFile);
}

function readTextIfExists(filePath) {
  try {
    if (!fs.existsSync(filePath)) return null;
    return fs.readFileSync(filePath, "utf8");
  } catch {
    return null;
  }
}

function parseEnvProperties(text) {
  /**
   * Tiny .properties parser (key=value, ignores blanks and #comments).
   */
  const obj = {};
  if (!text) return obj;
  for (const line of text.split(/\r?\n/)) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith("#")) continue;
    const eqIndex = trimmed.indexOf("=");
    if (eqIndex === -1) continue;
    const key = trimmed.slice(0, eqIndex).trim();
    const value = trimmed.slice(eqIndex + 1).trim();
    if (key) obj[key] = value;
  }
  return obj;
}

function toEnvPropertiesText(obj) {
  const keys = Object.keys(obj).sort();
  return keys.map((k) => `${k}=${obj[k]}`).join("\n") + "\n";
}

function mergeAllureResults() {
  if (!isDirectory(JAVA_RESULTS_DIR)) {
    throw new Error(`Java allure-results directory not found: ${JAVA_RESULTS_DIR}`);
  }
  if (!isDirectory(CSHARP_RESULTS_DIR)) {
    throw new Error(`C# allure-results directory not found: ${CSHARP_RESULTS_DIR}`);
  }

  rmDirIfExists(OUT_DIR);
  ensureDir(COMBINED_RESULTS_DIR);
  ensureDir(METRICS_DIR);

  // Copy flat files with prefix to avoid collisions (uuid files can collide across suites).
  const sources = [
    { name: "java", dir: JAVA_RESULTS_DIR },
    { name: "csharp", dir: CSHARP_RESULTS_DIR },
  ];

  for (const src of sources) {
    const files = listFilesFlat(src.dir);
    for (const filePath of files) {
      const base = path.basename(filePath);

      // We'll merge environment.properties separately.
      if (base === "environment.properties") continue;

      const dstBase = `${src.name}-${base}`;
      const dstPath = path.join(COMBINED_RESULTS_DIR, dstBase);
      copyFileSafe(filePath, dstPath);
    }
  }

  // Merge environment.properties into one, while preserving source-specific keys.
  // We also include a couple of synthetic keys to make it obvious multiple sources exist.
  const javaEnv = parseEnvProperties(
    readTextIfExists(path.join(JAVA_RESULTS_DIR, "environment.properties")),
  );
  const csharpEnv = parseEnvProperties(
    readTextIfExists(path.join(CSHARP_RESULTS_DIR, "environment.properties")),
  );

  const mergedEnv = {
    "central.dashboard.sources": "java,csharp",
    "central.dashboard.generatedAt": new Date().toISOString(),
  };

  for (const [k, v] of Object.entries(javaEnv)) mergedEnv[`java.${k}`] = v;
  for (const [k, v] of Object.entries(csharpEnv)) mergedEnv[`csharp.${k}`] = v;

  fs.writeFileSync(
    path.join(COMBINED_RESULTS_DIR, "environment.properties"),
    toEnvPropertiesText(mergedEnv),
    "utf8",
  );
}

function runAllureGenerate() {
  const res = spawnSync(
    "allure",
    ["generate", COMBINED_RESULTS_DIR, "-o", ALLURE_REPORT_DIR, "--clean"],
    { stdio: "inherit" },
  );

  if (res.error) {
    throw new Error(
      `Failed to run Allure CLI. Ensure allure is on PATH. Underlying error: ${res.error.message}`,
    );
  }
  if (res.status !== 0) {
    throw new Error(`Allure CLI exited with non-zero status: ${res.status}`);
  }
}

function safeJsonParse(text) {
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

function getLabelValue(labels, labelName) {
  if (!Array.isArray(labels)) return null;
  const match = labels.find((l) => l && l.name === labelName);
  return match ? match.value : null;
}

function normalizeStatus(status) {
  // Allure statuses: passed, failed, broken, skipped, unknown
  if (!status) return "unknown";
  const s = String(status).toLowerCase();
  if (["passed", "failed", "broken", "skipped", "unknown"].includes(s)) return s;
  return "unknown";
}

function inferBrowser(resultJson) {
  // Allure often uses labels: browser, browserName, or parameter called browser.
  const labels = resultJson.labels || [];
  const labelBrowser =
    getLabelValue(labels, "browser") ||
    getLabelValue(labels, "browserName") ||
    getLabelValue(labels, "browser.name");
  if (labelBrowser) return labelBrowser;

  // Some frameworks put it under parameters
  if (Array.isArray(resultJson.parameters)) {
    const p = resultJson.parameters.find(
      (x) =>
        x &&
        typeof x.name === "string" &&
        x.value &&
        ["browser", "browserName", "browser_name"].includes(x.name),
    );
    if (p) return String(p.value);
  }

  return "unknown";
}

function inferFramework(resultJson) {
  const labels = resultJson.labels || [];
  return (
    getLabelValue(labels, "framework") ||
    getLabelValue(labels, "language") ||
    "unknown"
  );
}

function computeMetrics() {
  const files = listFilesFlat(COMBINED_RESULTS_DIR).filter((p) =>
    p.endsWith("-result.json"),
  );

  const totals = {
    total: 0,
    passed: 0,
    failed: 0,
    broken: 0,
    skipped: 0,
    unknown: 0,
  };

  const bySource = {};
  const byBrowser = {};
  const byBrowserAndStatus = {};
  const byFramework = {};
  const examples = [];

  for (const filePath of files) {
    const base = path.basename(filePath);
    const source = base.startsWith("java-")
      ? "java"
      : base.startsWith("csharp-")
        ? "csharp"
        : "unknown";

    const json = safeJsonParse(fs.readFileSync(filePath, "utf8"));
    if (!json) continue;

    const status = normalizeStatus(json.status);
    const browser = inferBrowser(json);
    const framework = inferFramework(json);
    const name = json.fullName || json.name || base;

    totals.total += 1;
    totals[status] += 1;

    bySource[source] = bySource[source] || {
      total: 0,
      passed: 0,
      failed: 0,
      broken: 0,
      skipped: 0,
      unknown: 0,
    };
    bySource[source].total += 1;
    bySource[source][status] += 1;

    byBrowser[browser] = (byBrowser[browser] || 0) + 1;
    byFramework[framework] = (byFramework[framework] || 0) + 1;

    byBrowserAndStatus[browser] = byBrowserAndStatus[browser] || {
      total: 0,
      passed: 0,
      failed: 0,
      broken: 0,
      skipped: 0,
      unknown: 0,
    };
    byBrowserAndStatus[browser].total += 1;
    byBrowserAndStatus[browser][status] += 1;

    if (examples.length < 25 && (status === "failed" || status === "broken")) {
      examples.push({
        source,
        status,
        browser,
        framework,
        name,
      });
    }
  }

  const passRate = totals.total > 0 ? totals.passed / totals.total : 0;

  const summary = {
    generatedAt: new Date().toISOString(),
    paths: {
      javaResults: path.relative(REPO_ROOT, JAVA_RESULTS_DIR),
      csharpResults: path.relative(REPO_ROOT, CSHARP_RESULTS_DIR),
      combinedResults: path.relative(REPO_ROOT, COMBINED_RESULTS_DIR),
      allureReport: path.relative(REPO_ROOT, ALLURE_REPORT_DIR),
    },
    totals,
    passRate,
    bySource,
    byFramework,
    byBrowser,
    byBrowserAndStatus,
    failingExamples: examples,
    notes: [
      "Browser detection is best-effort based on common Allure label conventions. If no browser label exists, tests are grouped under 'unknown'.",
      "This is a centralized discovery-only dashboard; use the full Allure HTML report for detailed per-test timelines and attachments.",
    ],
  };

  fs.writeFileSync(
    path.join(METRICS_DIR, "summary.json"),
    JSON.stringify(summary, null, 2),
    "utf8",
  );

  return summary;
}

function escapeHtml(s) {
  return String(s)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function renderDashboardHtml(summary) {
  const totals = summary.totals;
  const pct = (x) => `${(x * 100).toFixed(1)}%`;

  const browserRows = Object.entries(summary.byBrowserAndStatus)
    .sort((a, b) => b[1].total - a[1].total)
    .map(([browser, stats]) => {
      const pr = stats.total > 0 ? stats.passed / stats.total : 0;
      return `<tr>
        <td>${escapeHtml(browser)}</td>
        <td class="num">${stats.total}</td>
        <td class="num ok">${stats.passed}</td>
        <td class="num bad">${stats.failed}</td>
        <td class="num warn">${stats.broken}</td>
        <td class="num muted">${stats.skipped}</td>
        <td class="num">${pct(pr)}</td>
      </tr>`;
    })
    .join("\n");

  const sourceRows = Object.entries(summary.bySource)
    .sort((a, b) => b[1].total - a[1].total)
    .map(([source, stats]) => {
      const pr = stats.total > 0 ? stats.passed / stats.total : 0;
      return `<tr>
        <td>${escapeHtml(source)}</td>
        <td class="num">${stats.total}</td>
        <td class="num ok">${stats.passed}</td>
        <td class="num bad">${stats.failed}</td>
        <td class="num warn">${stats.broken}</td>
        <td class="num muted">${stats.skipped}</td>
        <td class="num">${pct(pr)}</td>
      </tr>`;
    })
    .join("\n");

  const failingList = (summary.failingExamples || [])
    .map(
      (x) =>
        `<li><code>${escapeHtml(
          x.status,
        )}</code> [${escapeHtml(x.source)} | ${escapeHtml(
          x.browser,
        )}] ${escapeHtml(x.name)}</li>`,
    )
    .join("\n");

  const html = `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Central Allure Dashboard</title>
  <style>
    :root{
      --bg:#0b1220; --panel:#111a2e; --text:#e7eefc; --muted:#aab7d6;
      --ok:#4ade80; --bad:#fb7185; --warn:#fbbf24; --line:#22304f;
      --link:#93c5fd;
    }
    body{ margin:0; font-family: ui-sans-serif,system-ui,-apple-system,Segoe UI,Roboto,Arial;
      background:var(--bg); color:var(--text); }
    header{ padding:20px 22px; border-bottom:1px solid var(--line); background:linear-gradient(180deg,#0b1220,#0b1220 70%,#09101d); }
    h1{ margin:0 0 6px 0; font-size:18px; font-weight:700; }
    .sub{ color:var(--muted); font-size:13px; }
    main{ padding:18px 22px 28px; max-width:1100px; }
    a{ color:var(--link); }
    .grid{ display:grid; grid-template-columns:repeat(12,1fr); gap:12px; }
    .card{ grid-column:span 12; background:var(--panel); border:1px solid var(--line);
      border-radius:10px; padding:14px 14px 12px; }
    @media(min-width:900px){
      .card.half{ grid-column:span 6; }
    }
    .kpi{ display:flex; gap:14px; flex-wrap:wrap; }
    .kpi .item{ padding:10px 12px; border:1px solid var(--line); border-radius:10px; min-width:140px; background:#0d162a; }
    .kpi .label{ color:var(--muted); font-size:12px; }
    .kpi .value{ font-size:18px; font-weight:800; margin-top:4px; }
    table{ width:100%; border-collapse:collapse; font-size:13px; }
    th,td{ padding:9px 8px; border-bottom:1px solid var(--line); }
    th{ text-align:left; color:var(--muted); font-weight:700; }
    td.num{ text-align:right; font-variant-numeric: tabular-nums; }
    .ok{ color:var(--ok); }
    .bad{ color:var(--bad); }
    .warn{ color:var(--warn); }
    .muted{ color:var(--muted); }
    code{ background:#0d162a; border:1px solid var(--line); padding:2px 6px; border-radius:7px; color:var(--text); }
    ul{ margin:8px 0 0 20px; color:var(--muted); }
    .paths{ display:grid; grid-template-columns:1fr; gap:6px; margin-top:10px; }
  </style>
</head>
<body>
  <header>
    <h1>Central Allure Dashboard (Java + C#)</h1>
    <div class="sub">
      Generated at <code>${escapeHtml(summary.generatedAt)}</code> •
      Unified report: <a href="./allure-report/index.html">allure-report/index.html</a>
    </div>
  </header>

  <main>
    <div class="grid">
      <section class="card">
        <div class="kpi">
          <div class="item">
            <div class="label">Total</div>
            <div class="value">${totals.total}</div>
          </div>
          <div class="item">
            <div class="label">Passed</div>
            <div class="value ok">${totals.passed}</div>
          </div>
          <div class="item">
            <div class="label">Failed</div>
            <div class="value bad">${totals.failed}</div>
          </div>
          <div class="item">
            <div class="label">Broken</div>
            <div class="value warn">${totals.broken}</div>
          </div>
          <div class="item">
            <div class="label">Skipped</div>
            <div class="value muted">${totals.skipped}</div>
          </div>
          <div class="item">
            <div class="label">Pass rate</div>
            <div class="value">${pct(summary.passRate)}</div>
          </div>
        </div>

        <div class="paths">
          <div class="sub">Inputs:</div>
          <div class="sub">Java results: <code>${escapeHtml(summary.paths.javaResults)}</code></div>
          <div class="sub">C# results: <code>${escapeHtml(summary.paths.csharpResults)}</code></div>
          <div class="sub">Combined: <code>${escapeHtml(summary.paths.combinedResults)}</code></div>
          <div class="sub">Allure HTML: <code>${escapeHtml(summary.paths.allureReport)}</code></div>
          <div class="sub">Metrics JSON: <code>src/test/allure/dashboard/out/metrics/summary.json</code></div>
        </div>
      </section>

      <section class="card half">
        <h2 style="margin:0 0 10px 0; font-size:14px;">By source</h2>
        <table>
          <thead>
            <tr>
              <th>Source</th><th class="num">Total</th><th class="num">Passed</th><th class="num">Failed</th><th class="num">Broken</th><th class="num">Skipped</th><th class="num">Pass rate</th>
            </tr>
          </thead>
          <tbody>
            ${sourceRows}
          </tbody>
        </table>
      </section>

      <section class="card half">
        <h2 style="margin:0 0 10px 0; font-size:14px;">Cross-browser (best-effort)</h2>
        <table>
          <thead>
            <tr>
              <th>Browser</th><th class="num">Total</th><th class="num">Passed</th><th class="num">Failed</th><th class="num">Broken</th><th class="num">Skipped</th><th class="num">Pass rate</th>
            </tr>
          </thead>
          <tbody>
            ${browserRows}
          </tbody>
        </table>
        <div class="sub" style="margin-top:8px;">
          Note: if browser labels aren't present in raw results, tests are grouped under <code>unknown</code>.
        </div>
      </section>

      <section class="card">
        <h2 style="margin:0 0 10px 0; font-size:14px;">Examples of failures/broken (first 25)</h2>
        <ul>
          ${failingList || "<li class='muted'>None</li>"}
        </ul>
      </section>
    </div>
  </main>
</body>
</html>`;

  fs.writeFileSync(path.join(OUT_DIR, "dashboard.html"), html, "utf8");
}

function main() {
  console.log("Central Allure dashboard generator");
  console.log("Repo root:", REPO_ROOT);

  console.log("\n[1/4] Merging allure-results...");
  mergeAllureResults();
  console.log("Combined results:", COMBINED_RESULTS_DIR);

  console.log("\n[2/4] Computing metrics...");
  const summary = computeMetrics();
  console.log("Metrics JSON:", path.join(METRICS_DIR, "summary.json"));

  console.log("\n[3/4] Generating Allure HTML report...");
  runAllureGenerate();
  console.log("Allure report:", ALLURE_REPORT_DIR);

  console.log("\n[4/4] Writing dashboard HTML summary...");
  renderDashboardHtml(summary);
  console.log("Dashboard HTML:", path.join(OUT_DIR, "dashboard.html"));

  console.log("\nDone.");
}

main();
