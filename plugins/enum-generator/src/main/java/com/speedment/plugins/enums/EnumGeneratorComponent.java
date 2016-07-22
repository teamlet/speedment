package com.speedment.plugins.enums;

import com.speedment.common.injector.Injector;
import com.speedment.plugins.enums.internal.GeneratedEntityDecorator;
import static com.speedment.common.injector.State.INITIALIZED;
import static com.speedment.common.injector.State.RESOLVED;
import com.speedment.common.injector.annotation.ExecuteBefore;
import com.speedment.common.injector.annotation.Inject;
import com.speedment.common.injector.annotation.InjectorKey;
import com.speedment.common.injector.annotation.WithState;
import com.speedment.generator.StandardTranslatorKey;
import com.speedment.generator.component.CodeGenerationComponent;
import com.speedment.generator.component.EventComponent;
import com.speedment.generator.component.TypeMapperComponent;
import com.speedment.plugins.enums.internal.ui.CommaSeparatedStringPropertyItem;
import com.speedment.runtime.config.Table;
import com.speedment.runtime.internal.component.AbstractComponent;
import com.speedment.runtime.internal.license.AbstractSoftware;
import static com.speedment.runtime.internal.license.OpenSourceLicense.APACHE_2;
import com.speedment.runtime.license.Software;
import com.speedment.tool.component.UserInterfaceComponent;
import com.speedment.tool.config.ColumnProperty;
import com.speedment.tool.config.DocumentProperty;
import com.speedment.tool.event.TreeSelectionChange;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.TreeItem;

/**
 * A plugin for generating internal enums for columns marked as ENUM in the
 * database.
 * <p>
 * To use this plugin, add the following to your configuration tag in the
 * pom-file:
 * {@code <component>com.speedment.plugins.enums.EnumGeneratorComponent</component>}
 *
 * @author Emil Forslund
 * @author Simon Jonasson
 * @since 1.0.0
 */
@InjectorKey(EnumGeneratorComponent.class)
public final class EnumGeneratorComponent extends AbstractComponent {

    private @Inject
    EventComponent events;

    @ExecuteBefore(RESOLVED)
    void installDecorators(Injector injector,
        @WithState(INITIALIZED) TypeMapperComponent typeMappers,
        @WithState(INITIALIZED) CodeGenerationComponent codeGen){

        typeMappers.install(String.class, StringToEnumTypeMapper::new);
        codeGen.add(Table.class, StandardTranslatorKey.GENERATED_ENTITY, new GeneratedEntityDecorator(injector));

        events.on(TreeSelectionChange.class, ev -> {
            if (!ev.changeEvent().getList().isEmpty()) {
                final TreeItem<DocumentProperty> treeItem = ev.changeEvent().getList().get(0);
                if (treeItem != null) {
                    final DocumentProperty doc = treeItem.getValue();
                    if (doc instanceof ColumnProperty) {
                        final ColumnProperty col = (ColumnProperty) doc;
                        
                        final BooleanBinding isNotEnumMapper = Bindings.notEqual(col.typeMapperProperty(), StringToEnumTypeMapper.class.getName());
                        ev.properties().add(new CommaSeparatedStringPropertyItem(
                                col.enumConstantsProperty(),
                                col.getEnumConstants().orElse(null),
                                "Enum Constants",
                                "Used for defining what value the enum can take",
                                editor -> {
                                    editor.disableProperty().bind(isNotEnumMapper);
                                }
                            )
                        );
                    }
                }
            }
        });
    }

    @Override
    public Class<EnumGeneratorComponent> getComponentClass() {
        return EnumGeneratorComponent.class;
    }

    @Override
    public Software asSoftware() {
        return AbstractSoftware.with(
            "Enum Generator",
            "1.0.0",
            "Generate enum implementations for columns marked as ENUM in the database.",
            APACHE_2
        );
    }
}
