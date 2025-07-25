//Arquivo: Info.java
package com.JDStudio.Engine.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Uma anotação customizada para guardar informações sobre uma classe,
* como autor e versão.
*/
@Retention(RetentionPolicy.RUNTIME) // A anotação estará disponível em tempo de execução
@Target(ElementType.TYPE) // Esta anotação só pode ser aplicada a tipos (classes, interfaces)
public @interface Info {
 String autor() default "Desconhecido";
 String versao() default "1.0";
}