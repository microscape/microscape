package io.microscape.feign;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM6;

public class FeignVisitor extends ClassVisitor {

    private List<String> foundClients = new ArrayList<>(10);

    public FeignVisitor() {
        super(ASM6);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if ("Lorg/springframework/cloud/netflix/feign/FeignClient;".equals(desc) && visible) {
            return new FeignAnnotationVisitor();
        }
        return null;
    }

    public List<String> getFoundClients() {
        return foundClients;
    }

    class FeignAnnotationVisitor extends AnnotationVisitor {

        FeignAnnotationVisitor() {
            super(ASM6);
        }

        @Override
        public void visit(String name, Object value) {
            if("name".equals(name) || "value".equals(name)) {
              foundClients.add(String.valueOf(value));
            }
        }
    }
}
