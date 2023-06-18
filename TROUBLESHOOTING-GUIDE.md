# Apache Fortress Web TROUBLESHOOTING GUIDE
-------------------------------------------------------------------------------

## Document Overview

Common errors encountered during usage.
___________________________________________________________________________________
## Debug options:

WIP


## 1. Problem running web component in Apache Tomcat 9 using Java 17. 

When pulling up pages with tree view

```
Caused by: java.lang.reflect.InaccessibleObjectException: Unable to make field protected transient java.lang.Object javax.swing.tree.DefaultMutableTreeNode.userObject accessible: module java.desktop does not "opens javax.swing.tree" to unnamed module @7d3e0a57
```


Remedy: place the following statement in tomcat runtime env:

```
JDK_JAVA_OPTIONS="$JDK_JAVA_OPTIONS --add-opens=java.desktop/javax.swing.tree=ALL-UNNAMED"
```
