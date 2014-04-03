// 
// auto-generated class, don't edit (protoc generator version ${version})
//
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using com.turbospaces.protodise;
using com.turbospaces.protodise.gen;
using com.turbospaces.protodise.types;

namespace ${pkg} {
    <#assign members = enum.members>
    public enum ${enum.name} {
    <#list members.entrySet() as entry>
    ${entry.value} = ${entry.key}<#if entry_has_next>,</#if>
    </#list>
    }
}