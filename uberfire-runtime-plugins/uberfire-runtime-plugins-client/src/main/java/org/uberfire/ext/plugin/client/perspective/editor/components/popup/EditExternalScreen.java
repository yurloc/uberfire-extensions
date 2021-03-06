/*
* Copyright 2013 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.uberfire.ext.plugin.client.perspective.editor.components.popup;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.ext.plugin.client.perspective.editor.api.ExternalPerspectiveEditorComponent;
import org.uberfire.ext.plugin.client.perspective.editor.structure.EditorWidget;
import org.uberfire.ext.plugin.client.perspective.editor.structure.PerspectiveEditorUI;

public class EditExternalScreen extends Composite {

    private final ExternalPerspectiveEditorComponent externalComponent;
    private final EditorWidget parent;

    @UiField
    Modal popup;

    @UiField
    FlowPanel externalWidget;

    Listener listener;

    public interface Listener {
        void onSave();
        void onClose();
    }

    interface Binder
            extends
            UiBinder<Widget, EditExternalScreen> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public EditExternalScreen( EditorWidget parent,
            ExternalPerspectiveEditorComponent externalComponent,
            Listener listener) {
        initWidget( uiBinder.createAndBindUi (this ) );
        this.parent = parent;
        this.externalComponent = externalComponent;
        this.listener = listener;
        externalWidget.add( externalComponent.getConfig() );
        externalComponent.modalSettings( popup );
    }

    @UiHandler("cancelButton")
    void cancel( final ClickEvent event ) {
        popup.hide();
        if (listener != null) {
            listener.onClose();
        }
    }

    @UiHandler("okButton")
    void ok( final ClickEvent event ) {
        loadSavedParametersOnPerspectiveEditorUI();
        popup.hide();
        if (listener != null) {
            listener.onSave();
        }
    }

    private void loadSavedParametersOnPerspectiveEditorUI() {
        final IOCBeanDef<PerspectiveEditorUI> perspectiveEditorUIIOCBeanDef = IOC.getBeanManager().lookupBean( PerspectiveEditorUI.class );
        final PerspectiveEditorUI perspectiveEditorUI = perspectiveEditorUIIOCBeanDef.getInstance();
        perspectiveEditorUI.loadEditExternalComponentEvent( parent.hashCode() + "", externalComponent.getClass().getName(), externalComponent.getPlaceName(), externalComponent.getParametersMap() );
    }

    public void show() {
        popup.show();
    }
}
