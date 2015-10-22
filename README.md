# JRazor
JRazor Template Engine
a template engine render the dynamic template with compiled java code
the gramma just like c# razor，but it runs java
you can define a code block with "@" or output the variable with "@". it's easy if you're good at java,in another way,it is java.
this is a simple template: which will import package and out hello world
```java
@import java.text.MessageFormat; 
<html> 
<head> 
</head> 
<body> 
@* this is comment block,it allows mutilline,the @block in comment block will not be rendered *@ 
@{ 
//this is java code block,you can write comments just like java does 
String name=MessageFormat.format("{0} {1}","",""); 
String who="superstudio";//declare a who varian 

} 
<!--in the say way you can write html comment outside code block--> 
<!--and the line below would output "hellow,superstudio"--> 
Hello,@who 
</body> 
</html> 
```
this template will output a Html :
```html
<html> 
<head> 
</head> 
<body> 
<!--in the say way you can write html comment outside code block--> 
<!--and the line below would output "hellow,superstudio"--> 
Hello,superstudio 
</body> 
</html> 

```
