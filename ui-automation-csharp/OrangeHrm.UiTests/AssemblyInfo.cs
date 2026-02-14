using NUnit.Framework;

// Force non-parallel execution for demo stability.
[assembly: Parallelizable(ParallelScope.None)]
[assembly: LevelOfParallelization(1)]
