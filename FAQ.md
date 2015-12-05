# FAQ #

## Will Probatron validate XSD (or RELAX NG) schemas with embedded Schematron constraints? ##

No, Probatron applies only Schematron schemas to XML documents. If you have another schema with Schematron constraints, these need to be exracted to create a Schematron schema you can use.

## What version of Schematron does Probatron target? ##

Probatron targets the International Standard version of Schematron (ISO/IEC 19757-3:2006), and tracks (compatible) proposed enhancements in drafts of the forthcoming draft revision of that Standard.