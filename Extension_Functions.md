# XPath Extension Functions #

Probatron4J makes a number of custom XPath extension functions available.

These functions are in the Namespace "`http://www.xmlopen.org/functions`", which must be bound and used when using these functions. In this documentation the prefix "xo" is assumed to be so bound.

For example:

```
<?xml version="1.0"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron"
  xmlns:xo="http://www.probatron.org/functions">

 <!-- we bind the 'pr' prefix for use in function calls -->
 <sch:ns prefix="pr" uri="http://www.xmlopen.org/functions"/>

 <sch:pattern>
    <sch:rule id="isbn-test" flag="warn" context="isbn">
      <!-- using an extension function here -->
      <sch:assert test="xo:is-valid-isbn(.)">Not an ISBN</sch:assert>
    </sch:rule>
  </sch:pattern>

</sch:schema>

```


# Details #

## file-exists ##

`xo:file-exists($arg as xs:string) as xs:boolean`

Returns `true` if a file exists at the URL described by `$arg`.

The function will only return true when Probatron is invoked on content in a file system (i.e. the candidate document is as a file URL); otherwise the function always returns `false`.

Note: if `$arg` is a relative URL, it is evaluated relative to the candidate document.


### governing-dtd-public-identifier ###

`xo:governing-dtd-public-identifier() as xs:string`

Returns the public identifier of any DTD governing the candidate as given in its document type declaration.



### governing-dtd-system-identifier ###

`xo:governing-dtd-system-identifier() as xs:string`

Returns the system identifier of any DTD governing the candidate as given in its document type declaration.


## is-valid-isbn ##

`xo:is-valid-isbn($arg as xs:string) as xs:boolean`

Returns `true` if `$arg` is a lexically valid 10 digit ISBN with a valid checksum digit; otherwise returns `false`.


## is-valid-isbn-13 ##

`xo:is-valid-isbn-13($arg as xs:string) as xs:boolean`

Returns `true` if `$arg` is a lexically valid 13 digit ISBN with a valid checksum digit; otherwise returns `false`.


## is-valid-issn ##

`xo:is-valid-issn($arg as xs:string) as xs:boolean`

Returns `true` if `$arg` is a lexically valid ISSN with a valid checksum digit; otherwise returns `false`.


## roman-numeral-to-decimal ##

`xo:roman-numeral-to-decimal($arg as xs:string) as xs:string`

Returns a string containing the decimal number equivalent of the roman numeral value contained in `$arg`, on "NaN" if a value cannot be determined.


## system-id ##

`xo:system-id() as xs:string`

Returns the system identifier of the candidate document being validated.



## url-mime-type ##

`xo:url-mime-type($arg1 as xs:string, $arg2 as node()?]) as xs:string`

Returns an indication of the MIME type of the resource at the URL `$arg1`, optionally evaluated in the context of the URI of the node `$arg2`.

Based on sniffing the first few bytes of the indicated resource, this function will return a MIME type string if the resource has opening bytes consistent with the following common formats:

  * application/xml
  * application/pdf
  * application/postscript
  * application/zip
  * image/gif
  * image/jpeg
  * image/png
  * image/tiff

If none of the above can be determined, an empty string is returned.