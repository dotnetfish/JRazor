# JRazor
JRazor Template Engine
a template engine render the dynamic template with compiled java code
the gramma just like c# razor，but it runs java.<br/>
you can define a code block with "@" or output the variable with "@". it's easy if you're good at java,in another way,it is java.
## Quick start

### Render Template
```java
// first init your template host envirenments,you could set it be singleton and init the hostContext when your app started
 HostBuilder builder=new HostBuilder();
 WebTemplateHost templateHost=new WebTemplateHost();
 HostContext host= builder.useHost(templateHost)
                          .useAutoCompiler(templateHost.mapPath("/web-inf/templates"))
                          .useTemplateEngine(new RazorTemplateEngine())
                          .build();

```
### Render Template
```java
  String result=engine.renderTemplate(renderContext);
  
        TemplateInfo template=new TemplateInfo();//or load templateInfo from database
        template.setTemplateCategory("home");
        template.setTemplateName(name);
        Map<String, Object> templateData=new HashMap<String,Object>();
        TemplateDataDictionary data=new TemplateDataDictionary(null);
        templateData.put("myVariant","variantValue");
        data.setTemplateData(templateData);//in the the template @get("myVariant") would output variantValue
        data.setModel(new ModelEntry());//set a  template page model entry,then you can access it by @getModel()
        JRazorTemplateEngine.render(template,data,writer);
        String result1=writer.toString();
}
```

## template demo
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
String who="superstudio";//declared  who variant

} 
<!--in the say way you can write html comments outside code block--> 
<!--and the line below would output "hello,superstudio"--> 
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
<!--in the say way you can write html comments outside code block--> 
<!--and the line below would output "hello,superstudio"--> 
Hello,superstudio 
</body> 
</html> 

```
