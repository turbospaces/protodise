// 
// auto-generated class, don't edit (protoc generator version ${version})
//
package ${pkg};

import com.turbospaces.protodise.gen.*;
import com.turbospaces.protodise.*;

<#assign members = enum.members>
public enum ${enum.name} implements GeneratedEnum {
    <#list members.entrySet() as entry>
    ${entry.value} (${entry.key})<#if entry_has_next>,<#else>;</#if>
    </#list>

    private int tag;
    private ${enum.name}(int tag) {
       this.tag = tag;
    }
    @Override
    public int tag() {return tag;}
    @Override
    public int getClassId() {
       return CLASS_ID;
    }
    public static final int CLASS_ID = Misc.hash32(${enum.name}.class.getName());
}