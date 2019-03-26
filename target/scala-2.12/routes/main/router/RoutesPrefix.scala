// @GENERATOR:play-routes-compiler
// @SOURCE:/home/vai/Documents/Proftaak/BackEnd-Bijbaan/conf/routes
// @DATE:Tue Mar 19 16:00:14 CET 2019


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
