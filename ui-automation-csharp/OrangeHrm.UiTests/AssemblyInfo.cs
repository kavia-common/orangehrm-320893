using NUnit.Framework;

// Force non-parallel execution for demo stability.
//
// Notes:
// - ParallelScope.None disables NUnit-level parallelization.
// - LevelOfParallelism(1) ensures NUnit will not schedule more than 1 worker even if parallelization
//   is enabled elsewhere.
[assembly: Parallelizable(ParallelScope.None)]
[assembly: LevelOfParallelism(1)]
