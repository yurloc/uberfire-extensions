/*
 * Copyright 2014 JBoss Inc
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
package org.uberfire.ext.wires.core.client.palette;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "WiresPaletteScreen")
public class PaletteScreen extends Composite {

    interface ViewBinder extends UiBinder<Widget, PaletteScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    public SimplePanel categoryShapes;

    @UiField
    public SimplePanel categoryFixedShapes;

    @UiField
    public SimplePanel categoryConnectors;

    @UiField
    public SimplePanel categoryContainers;

    @Inject
    private ShapesGroup shapesGroup;

    @Inject
    private FixedShapesGroup fixedShapesGroup;

    @Inject
    private ConnectorsGroup connectorsGroup;

    @Inject
    private ContainersGroup containersGroup;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        categoryShapes.setWidget( shapesGroup );
        categoryFixedShapes.setWidget( fixedShapesGroup );
        categoryConnectors.setWidget( connectorsGroup );
        categoryContainers.setWidget( containersGroup );
    }

    @WorkbenchPartTitle
    @Override
    public String getTitle() {
        return "Palette";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

}