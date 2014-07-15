${class_name} ${from_var} = ${from_parent_var}.${getter}();
if(${from_var} != null ){
    ${to_parent_var}.${setter}(${from_var}());
}
