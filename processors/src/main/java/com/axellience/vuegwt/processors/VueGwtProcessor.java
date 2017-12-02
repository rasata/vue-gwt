package com.axellience.vuegwt.processors;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.JsComponent;
import com.axellience.vuegwt.core.annotations.directive.Directive;
import com.axellience.vuegwt.core.annotations.style.Style;
import com.axellience.vuegwt.processors.component.ComponentJsTypeGenerator;
import com.axellience.vuegwt.processors.component.factory.VueComponentFactoryGenerator;
import com.axellience.vuegwt.processors.component.factory.VueJsComponentFactoryGenerator;
import com.axellience.vuegwt.processors.directive.VueDirectiveOptionsGenerator;
import com.axellience.vuegwt.processors.style.StyleProviderGenerator;
import com.axellience.vuegwt.processors.template.ComponentTemplateGenerator;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.Set;

import static com.axellience.vuegwt.core.generation.ComponentGenerationUtil.hasTemplate;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
    "com.axellience.vuegwt.core.annotations.component.Component",
    "com.axellience.vuegwt.core.annotations.component.JsComponent",
    "com.axellience.vuegwt.core.annotations.directive.Directive",
    "com.axellience.vuegwt.core.annotations.style.Style"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class VueGwtProcessor extends AbstractProcessor
{
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        this.processStyleAnnotations(roundEnv);
        this.processDirectiveAnnotations(roundEnv);
        this.processComponentAnnotations(roundEnv);
        this.processJsComponentAnnotations(roundEnv);

        // claim the annotation
        return true;
    }

    private void processStyleAnnotations(RoundEnvironment roundEnv)
    {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Style.class);

        StyleProviderGenerator styleProviderGenerator = new StyleProviderGenerator(processingEnv);
        for (TypeElement element : ElementFilter.typesIn(annotatedElements))
        {
            styleProviderGenerator.generate(element);
        }
    }

    private void processDirectiveAnnotations(RoundEnvironment roundEnv)
    {
        Set<? extends Element> annotatedElements =
            roundEnv.getElementsAnnotatedWith(Directive.class);

        VueDirectiveOptionsGenerator vueDirectiveOptionsGenerator =
            new VueDirectiveOptionsGenerator(processingEnv);
        for (TypeElement element : ElementFilter.typesIn(annotatedElements))
        {
            vueDirectiveOptionsGenerator.generate(element);
        }
    }

    private void processComponentAnnotations(RoundEnvironment roundEnv)
    {
        Set<? extends Element> componentElements =
            roundEnv.getElementsAnnotatedWith(Component.class);

        ComponentJsTypeGenerator componentJsTypeGenerator =
            new ComponentJsTypeGenerator(processingEnv);
        ComponentTemplateGenerator componentTemplateGenerator =
            new ComponentTemplateGenerator(processingEnv);
        VueComponentFactoryGenerator vueFactoryGenerator =
            new VueComponentFactoryGenerator(processingEnv);

        for (TypeElement componentType : ElementFilter.typesIn(componentElements))
        {
            if (hasTemplate(processingEnv, componentType))
                componentTemplateGenerator.generate(componentType);

            vueFactoryGenerator.generate(componentType);
            componentJsTypeGenerator.generate(componentType);
        }
    }

    private void processJsComponentAnnotations(RoundEnvironment roundEnv)
    {
        Set<? extends Element> annotatedElements =
            roundEnv.getElementsAnnotatedWith(JsComponent.class);

        VueJsComponentFactoryGenerator vueJsComponentRegistrationGenerator =
            new VueJsComponentFactoryGenerator(processingEnv);

        for (TypeElement element : ElementFilter.typesIn(annotatedElements))
        {
            vueJsComponentRegistrationGenerator.generate(element);
        }
    }
}