package com.speedment.runtime.internal.field.finder;

import com.speedment.runtime.exception.SpeedmentException;
import com.speedment.runtime.field.IntField;
import com.speedment.runtime.manager.Manager;

/**
 * @param <ENTITY>    entity type
 * @param <FK_ENTITY> foreign entity type
 * 
 * @author Emil Forslund
 * @since  3.0.0
 */
public final class FindFromInt<ENTITY, FK_ENTITY>  extends AbstractFindFrom<ENTITY, FK_ENTITY, IntField<ENTITY, ?>, IntField<FK_ENTITY, ?>> {
    
    public FindFromInt(IntField<ENTITY, ?> source, IntField<FK_ENTITY, ?> target, Manager<FK_ENTITY> manager) {
        super(source, target, manager);
    }
    
    @Override
    public FK_ENTITY apply(ENTITY entity) {
        final int value = getSourceField().getter().getAsInt(entity);
        return getTargetManager().findAny(getTargetField(), value)
            .orElseThrow(() -> new SpeedmentException(
                "Error! Could not find any " + 
                getTargetManager().getEntityClass().getSimpleName() + 
                " with '" + getTargetField().identifier().columnName() + 
                "' = '" + value + "'."
            ));
    }
}