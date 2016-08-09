package agent;

import java.lang.Class;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.annotation.Annotation;
import java.security.ProtectionDomain;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.NotFoundException;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        final String variant = agentArgs;

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
                // TODO: Set a global vars
                if (variant == null) {
                    return null;
                }

                ClassPool cp = ClassPool.getDefault();
                String clsFullName = s.replace("/", ".");
                CtClass cc = null;
                boolean isOriginal = false;

                // Load class
                try { 
                    cc = cp.get(clsFullName);
                    isOriginal = Agent.isOriginalClass(cc);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (isOriginal) {

                    CtClass ccVar = null;
                    String clsPackageName = cc.getPackageName();
                    String clsVarName = "";
                    boolean hasVariant = true;

                    /*
                     * Loooking for the variant class
                     * If the variant class existed, then manupulate the bytecode.
                     * Else, just let the original class run as usual
                     */
                    try {
                        clsVarName = clsPackageName + "." + variant + cc.getSimpleName();
                        ccVar = cp.get(clsVarName);
                    } catch (NotFoundException e) {
                        hasVariant = false;
                    }

                    if (hasVariant) {
                        /* 
                         * Create orginal ghost class file
                         * Ghost class is a cloned class of original file
                         * The implement of this class allows the inheritance is still working 
                         * on variant class.
                         */
                        try {
                            String clsOrgName = clsPackageName + ".Original" + cc.getSimpleName();
                            Class orgClass = cp.getAndRename(clsFullName, clsOrgName).toClass();
                            CtClass orgCc = cp.get(orgClass.getName());

                            // Set super class of variant class
                            ccVar.setSuperclass(orgCc);

                            /* 
                             * Override constructions and methods
                             * The idea is in each method, we will 
                             */
                            CtField fVarIns = new CtField(ccVar, "variantInstance", cc);
                            cc.addField(fVarIns);

                            String varInsMethodString = String.format("public %s getVariantInstance() { if (this.variantInstance == null) { this.variantInstance = new %s(); } return this.variantInstance;}; ", clsVarName, clsVarName);
                            CtMethod mVarIns = CtNewMethod.make(varInsMethodString, cc);
                            cc.addMethod(mVarIns);
                            // TODO: Need to override all contructions
                            // TODO: Need to override all declared method of the class
                            CtMethod m = cc.getDeclaredMethod("render");
                            String modBody = String.format("{ this.getVariantInstance(); return this.variantInstance.render(); }");
                            m.setBody(modBody);
                        } catch (javassist.NotFoundException e1) {
                            e1.printStackTrace();
                        } catch (javassist.CannotCompileException e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        // Do nothing
                        System.out.println(String.format("%s has no variant class for variant `%s` => %s", s, variant, clsVarName));
                    }

                    /*
                     * Return manipulated bytecode
                     */
                    try {
                        byte[] byteCode = cc.toBytecode();
                        cc.detach();
                        return byteCode;
                    } catch (java.io.IOException eio) {
                        return null;
                    } catch (javassist.CannotCompileException ec) {
                        return null;
                    }
                } else {
                    // System.out.println(String.format("%s is not OriginalClass", clsFullName));
                }

                return null;
            }
        });
    }


    public static boolean isOriginalClass(CtClass cc) throws java.lang.ClassNotFoundException{
        Object[] all = cc.getAnnotations();
        return "activities.FeaturedActivity".equals(cc.getName());
    }
}

