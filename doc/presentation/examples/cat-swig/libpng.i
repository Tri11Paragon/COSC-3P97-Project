%module LibPng

%{ #include "png.h" %}

%include "typemaps.i"
%include "enums.swg"
%include "std_string.i"
%include "various.i"
%include "arrays_java.i"

%import "limits.h"
%import "stddef.h"
%import "pngconf.h"
%import "pnglibconf.h"

%javaconst(1);

%apply unsigned int { uint32_t };
%apply int { int32_t };
%apply unsigned short { uint16_t };
%apply short { int16_t };

%apply unsigned int* { uint32_t* };
%apply int* { int32_t* };
%apply unsigned short* { uint16_t* };
%apply short* { int16_t* };

%pragma(java) jniclasscode=%{
	static {
		System.loadLibrary("png");
		System.loadLibrary("libpng_wrap");
	}
%}

%rename("%(lowercamelcase)s", %$isfunction) "";
%rename("%(lowercamelcase)s", %$isvariable) "";

%rename("%(uppercase)s", %$isconstant) "";
%rename("%(uppercase)s", %$isvariable, %$isstatic) "";
%rename("%(uppercase)s", %$isenumitem) "";

%rename("%(camelcase)s", %$isclass) "";

// %typemap(javain) byte[] buffer "void * buffer"
// %typemap(javaout) void* buffer "byte[] buffer"
// %typemap(in) (void * buffer) {
// 	$1 = $input;
// };

// Map void* buffer to byte[]
%typemap(jni) void * buffer "jbyteArray"
%typemap(jtype) void * buffer "byte[]"
%typemap(jstype) void * buffer "byte[]"

%typemap(javain) void * buffer "$javainput"
%typemap(javaout) void * buffer {
	return $jnicall;
}

%include "png.h"
