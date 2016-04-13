# sfseize-api

This is a simple project that defines a proposed API for working with
space-filling curves (and their attendant utility classes) for the
purposes of multi-dimensional indexing in NoSQL data stores.

For an example of an existing space-filling curve library, please
see LocationTech's [SFCurve](https://github.com/locationtech/sfcurve) project
on GitHub.  (Full disclosure:  I'm a committer on that project, and hope
that parts of this API may some day be useful to SFCurve.)

## coarse development status by section

| area | API | example implementation |
| ---- | ----- |
| discretizer | Yes | Yes |
| space       | Yes | Yes |
| curve       | No  | No  |

## issues and developer notes

1.  The issue of floating-point precision for discretization and bin ranges
needs to be reconsidered.  Having assumed that the end-points are calculated without
error will turn out to be a Bad Idea.

