#Code to JavaDoc

CodeJavaDoc is a tool to connect Java code to JavaDoc parts. Code is copied into JavaDoc. There is support for command-line, maven and eclipse.</div>

Java file is only updated if the code part is changed, update is controlled by a MD5 checksum.</div>

It's only void methods that can be used.

#Code JavaDoc part
Formate:
========
 	/**
	 * <!-- code start[<method to include>] [<MD5 checksum>]-->;
	 * <!-- code end -->;
	 */


Just add the code start and code end tags and then the
code in the method will be copied between tags.

Note: MD5 checksum can be omitted.

Example:
--------
You have the method...

	package codejavadoc;
	...
	 class ExampleClass {
	  public void exampleMethod() {
	  System.out.println("Run example.");
	  }
	 }

...that you want to copy into your JavaDoc, add:
...

	/**
	 * <!-- code start[codejavadoc.ExampleClass.exampleMethod] -->
	 * <!-- code end -->
	 */
	 public void someMethod() {
	...

you get:
	
	/**
	 * <!-- code start[codejavadoc.ExampleClass.exampleMethod] [ADF56579088773675DEFCC]-->
	  System.out.println("Run example.");
	 * <!-- code end -->
	 */

You can update the JavaDoc by adding * or other decorations. This is because the formation of JavaDoc won't get overwritten if method isn't changed.

However keep in mind that it's going to be overwritten every time methods are changed or MD5 checksums are changed.
Command line
============
Usage:
--------

	Usage: CodeJavaDoc [-e <encoding>] src_dir
	
note: if encoding is omitted UTF-8 is used by default

Example
-------

	java -cp codejavadoc.jar CodeJavaDoc -e utf-8 /home/username/src/codejavadoc
	
#Maven


**groupId:** com.github.podal.codejavadoc

**artifactId:** codejavadoc

**version:** 1.0.0

Example:
--------
	mvn codejavadoc:codejavadoc
#Eclipse

<a href="http://podal.github.com/codejavadoc/eclipse/">codejavadoc eclipse</a>
Versions:

CodeJavaDoc version [Eclipse version]

codejavadoc-1.0.0-SNAPSHOT [com.github.podal.codejavadoc_1.0.10000.jar]

