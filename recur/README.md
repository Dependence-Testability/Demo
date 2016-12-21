# Recursive-Decomposition
Algorithm to find the average length of paths from point A to point B. 
Works on graphs with strongly connected components and with directed acyclic graphs.
Algorithm recursively decomposes a graph into a directed acyclic graph.
For COSC-490 Dependence &amp; Testability

## Current Status
- Implemented Tarjan's Algorithm for discovering Strongly Connected Components.
- Recursively decomposes sets of strongly connected components and combines relevant edges into "super edges".
- Runs a modified version of average length of paths for directed acyclic graph which makes use of "super edges" to avoid cycles or recounting paths.
