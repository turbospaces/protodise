// 
// auto-generated class, don't edit (protoc generator version ${version})
//
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.CompilerServices;
using com.turbospaces.protodise;
using com.turbospaces.protodise.gen;
using com.turbospaces.protodise.types;

namespace ${pkg} {
    <#assign fields = clazz.fieldDescriptors>
    public class ${clazz.name} : <#if clazz.parent??>${clazz.parent}<#else><#if clazz.exception>ApplicationException, GeneratedMessage<#else>GeneratedMessage</#if></#if> {
        <#list fields.entrySet() as entry>
        <#assign v = entry.value>
        <#assign k = entry.key>
        public static readonly int FIELD_${v.name?upper_case} = ${k};
        </#list>
        
        private static readonly ICollection<FieldDescriptor> DESCRIPTORS = new HashSet<FieldDescriptor>();
        private static ICollection<FieldDescriptor> ALL_DESCRIPTORS;
        
        <#list fields.entrySet() as entry>
        <#assign v = entry.value>
        <#assign k = entry.key>
        <#assign t = entry.value.type>
        public static readonly FieldDescriptor FIELD_DESCRIPTOR_${v.name?upper_case} = <#rt> 
        <#lt><#if t.isMap()>new FieldDescriptor(${v.tag}, "${v.name}", new MapMessageType(FieldType.${t.keyType}, "${t.keyTypeReference}", FieldType.${t.valueType}, "${t.valueTypeReference}"));
        <#lt><#elseif t.isCollection()>new FieldDescriptor(${v.tag}, "${v.name}", new CollectionMessageType(FieldType.${t.type}, "${t.typeReference}", ${t.set?c}));
        <#lt><#else>new FieldDescriptor(${v.tag}, "${v.name}", new ObjectMessageType(FieldType.${t.type}, "${t.typeReference}"));
        </#if>
        </#list>
        
        static ${clazz.name}() {
          <#list fields.entrySet() as entry>
          <#assign v = entry.value>
          DESCRIPTORS.Add(FIELD_DESCRIPTOR_${v.name?upper_case});
          </#list>
        }
         
        <#list fields.entrySet() as entry>
        <#assign v = entry.value>
        public ${v.type.csharpTypeAsString()} ${v.name} { get; set; }
        </#list>
        
        public <#if clazz.parent??>override<#else>virtual</#if> Object getFieldValue(int tag) {
            <#if clazz.parent??>
            FieldDescriptor d = base.getFieldDescriptor(tag);
            if( d != null ) return base.getFieldValue(tag);
            </#if>
            switch(tag) {
               <#list fields.entrySet() as entry>
               <#assign v = entry.value>
               <#assign k = entry.key>
               case ${k} : return this.${v.name};
               </#list>
               default : throw new SystemException("there is no such field with tag = " + tag);
            }
        }
        public <#if clazz.parent??>override<#else>virtual</#if> void setFieldValue(int tag, Object value) {
            <#if clazz.parent??>
            FieldDescriptor d = base.getFieldDescriptor(tag);
            if( d != null ) { base.setFieldValue(tag, value); return;}
            </#if>
            switch(tag) {
               <#list fields.entrySet() as entry>
               <#assign v = entry.value>
               <#assign k = entry.key>
               case ${k} : { this.${v.name} = (${v.type.csharpTypeAsString()}) value; break; }
               </#list>
               default : throw new SystemException("there is no such field with tag = " + tag);
            }
        }
        public <#if clazz.parent??>override<#else>virtual</#if> FieldDescriptor getFieldDescriptor(int tag) {
            <#if clazz.parent??>
            FieldDescriptor d = base.getFieldDescriptor(tag);
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
        [MethodImpl(MethodImplOptions.Synchronized)]
        public <#if clazz.parent??>override<#else>virtual</#if> ICollection<FieldDescriptor> getAllDescriptors() {
           ICollection<FieldDescriptor> all = ALL_DESCRIPTORS;
           if(all == null) {
              all = new LinkedList<FieldDescriptor>();
              <#if clazz.parent??>
              foreach (FieldDescriptor item in base.getAllDescriptors()) {       
                  all.Add(item);
              }       
              </#if>
              foreach (FieldDescriptor item in DESCRIPTORS) {       
                  all.Add(item);
              }
           }
           ALL_DESCRIPTORS = all;
           return all;
        }
    }
}