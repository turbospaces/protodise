// 
// auto-generated class, don't edit (protoc generator version ${version})
//
package ${pkg};

import java.util.*;
import com.turbospaces.protodise.*;

public class ${proto.name} {
   public static Collection<MessageDescriptor> MESSAGE_DESCRIPTORS = new HashSet<MessageDescriptor>();
   public static Collection<EnumDescriptor> ENUM_DESCRIPTORS = new HashSet<EnumDescriptor>();

   // MESSAGES
   <#list proto.messages as m>
   public static final MessageDescriptor ${m.name?upper_case} = new MessageDescriptor("${m.name}", <#if m.parent??>"${m.parent}"<#else>null</#if>, "${m.pkg}");
   </#list>
   // ENUMS
   <#list proto.enums as e>
   public static final EnumDescriptor ${e.name?upper_case} = new EnumDescriptor("${e.name}", "${e.pkg}");
   </#list>
   
   static {
       <#list proto.messages as m>
       MESSAGE_DESCRIPTORS.add(${m.name?upper_case});
       </#list>
       //
       <#list proto.enums as e>
       ENUM_DESCRIPTORS.add(${e.name?upper_case});
       </#list>
       //
       MESSAGE_DESCRIPTORS = Collections.unmodifiableCollection(MESSAGE_DESCRIPTORS);
       ENUM_DESCRIPTORS = Collections.unmodifiableCollection(ENUM_DESCRIPTORS);
   }
   
   <#list proto.constants as c>
   public static final ${c.type.javaTypeAsString()} ${c.name?upper_case} = ${c.value.toString()};
   </#list>
   
   public static void registerAll(MessageRegistry registry) {
       registry.registerAll( MESSAGE_DESCRIPTORS, ENUM_DESCRIPTORS );
   }
}