package ${package};

import org.springframework.stereotype.Component;

import ${fromClass};
import ${toClass};

@Component
public class ${fromClassName}Mapper{

    public ${toClassName} map(${fromClassName} ${fromClassVarName}){
        ${toClassName} ${toClassVarName} = new ${toClassName}();
        
        ${nestedObjects}
        
        ${mappingMethods}
        
        return ${toClassVarName};
    }

}
