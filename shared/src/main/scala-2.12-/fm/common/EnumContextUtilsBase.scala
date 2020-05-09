package fm.common

import fm.common.EnumContextUtils.Context

private[common] trait EnumContextUtilsBase {
  /**
   * Returns a named arg extractor
   */
  final def namedArg(c: Context) = c.universe.AssignOrNamedArg
}