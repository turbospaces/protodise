// 
// auto-generated class, don't edit (protoc generator version ${version})
//
package ${pkg};

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import com.turbospaces.protodise.MessageDescriptor.*;
import com.turbospaces.protodise.*;
import com.turbospaces.protodise.gen.*;
import com.turbospaces.protodise.types.*;

<#assign fields = clazz.fieldDescriptors>
public class ${clazz.name} extends <#if clazz.parent??>${clazz.parent}<#else><#if clazz.exception>AbstractGeneratedException<#else>AbstractGeneratedMessage</#if></#if> {
    public static final int CLASS_ID = Misc.hash32(${clazz.name}.class.getName());

    <#list fields.entrySet() as entry>
    <#assign v = entry.value>
    <#assign k = entry.key>
    public static final int FIELD_${v.name?upper_case} = ${k};
    </#list>
    
    private static final Collection<FieldDescriptor> DESCRIPTORS = new HashSet<FieldDescriptor>();
    private static final AtomicReference<Collection<FieldDescriptor>> ALL_DESCRIPTORS = new AtomicReference<Collection<FieldDescriptor>>();
    
    <#list fields.entrySet() as entry>
    <#assign v = entry.value>
    <#assign k = entry.key>
    <#assign t = entry.value.type>
    public static final FieldDescriptor FIELD_DESCRIPTOR_${v.name?upper_case} = <#rt> 
        <#lt><#if t.isMap()>new FieldDescriptor(${v.tag}, "${v.name}", new MapMessageType(FieldType.${t.keyType}, "${t.keyTypeReference}", FieldType.${t.valueType}, "${t.valueTypeReference}"));
        <#lt><#elseif t.isCollection()>new FieldDescriptor(${v.tag}, "${v.name}", new CollectionMessageType(FieldType.${t.elementType}, "${t.elementTypeReference}", ${t.set?c}));
        <#lt><#else>new FieldDescriptor(${v.tag}, "${v.name}", new ObjectMessageType(FieldType.${t.type}, "${t.typeReference}"));
        </#if>
    </#list>
    
    static {
     <#list fields.entrySet() as entry>
     <#assign v = entry.value>
       DESCRIPTORS.add(FIELD_DESCRIPTOR_${v.name?upper_case});
     </#list>
    }
    
    <#list fields.entrySet() as entry>
    <#assign v = entry.value>
    private ${v.type.javaTypeAsString()} ${v.name};
    </#list>
    
    <#list fields.entrySet() as entry>
    <#assign v = entry.value>
    public void set${v.name?cap_first}(${v.type.javaTypeAsString()} val) {
       this.${v.name} = val;
    }
    public ${v.type.javaTypeAsString()} get${v.name?cap_first}() {
       return this.${v.name};
    }
    </#list>
    @Override
    public Object getFieldValue(int tag) {
        <#if clazz.parent??>
        FieldDescriptor d = super.getFieldDescriptor(tag);
        if( d != null ) return super.getFieldValue(tag);
        </#if>
        switch(tag) {
           <#list fields.entrySet() as entry>
           <#assign v = entry.value>
           <#assign k = entry.key>
           case ${k} : return this.${v.name};
           </#list>
           default : throw new RuntimeException("there is no such field with tag = " + tag);
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public void setFieldValue(int tag, Object value) {
        <#if clazz.parent??>
        FieldDescriptor d = super.getFieldDescriptor(tag);
        if( d != null ) { super.setFieldValue(tag, value); return;}
        </#if>
        switch(tag) {
           <#list fields.entrySet() as entry>
           <#assign v = entry.value>
           <#assign k = entry.key>
           case ${k} : { this.${v.name} = (${v.type.javaTypeAsString()}) value; break; }
           </#list>
           default : throw new RuntimeException("there is no such field with tag = " + tag);
        }
    }
    @Override
    public FieldDescriptor getFieldDescriptor(int tag) {
        <#if clazz.parent??>
        FieldDescriptor d = super.getFieldDescriptor(tag);
        if( d != null ) return d;
        </#if>
        switch(tag) {
           <#list fields.entrySet() as entry>
           <#assign v = entry.value>
           <#assign k = entry.key>
           case ${k} : { return FIELD_DESCRIPTOR_${v.name?upper_case}; }
           </#list>
           default : return null;
        }
    }
    @Override
    public Collection<FieldDescriptor> getAllDescriptors() {
       Collection<FieldDescriptor> all = ALL_DESCRIPTORS.get();
       if(all == null) {
          all = new LinkedList<FieldDescriptor>();
          <#if clazz.parent??>       
          all.addAll(super.getAllDescriptors());       
          </#if>
          all.addAll(DESCRIPTORS);
       }
       ALL_DESCRIPTORS.set(all);
       return all;
    }
    @Override
    public int getClassId() {
       return CLASS_ID;
    }
}