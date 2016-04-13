# sfseize-api

This is a simple project that defines a proposed API for working with
space-filling curves (and their attendant utility classes) for the
purposes of multi-dimensional indexing in NoSQL data stores.

For an example of an existing space-filling curve library, please
see LocationTech's [SFCurve](https://github.com/locationtech/sfcurve) project
on GitHub.  (Full disclosure:  I'm a committer on that project, and hope
that parts of this API may some day be useful to SFCurve.)

## coarse development status by section

| area | API | example implementation | notes |
| ---- | :---: | :-----: | ----- |
| discretizer | Yes | Yes | translates between user space and index space |
| field range | Yes | Yes | a contiguous subset of an ordered field |
| space       | Yes | Yes | a collection of one or more field ranges that together define a sub-space |
| dimension   | Kinda' | No | needs to include more metadata? standard type information? |
| curve       | No  | No  | keep this lossless, operating strictly within index-space; helpers can be used to translate to/from user space |
| query       | No  | No  | this needs to include not only routines for correctness, but for short-circuiting to be faster at the cost of more false positives |

## issues and developer notes

1.  The issue of floating-point precision for discretization and bin ranges
needs to be reconsidered.  Having assumed that the end-points are calculated without
error will turn out to be a Bad Idea.

