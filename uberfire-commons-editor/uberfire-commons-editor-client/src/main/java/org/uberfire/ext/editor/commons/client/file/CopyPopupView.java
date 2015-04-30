/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.editor.commons.client.file;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import org.uberfire.ext.editor.commons.client.resources.CommonImages;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;

public class CopyPopupView extends FormStylePopup implements CopyPopup.View {

    final private TextBox nameTextBox = new TextBox();
    final private TextBox checkInCommentTextBox = new TextBox();
    private final Button copy;
    private final Button cancel;

    public CopyPopupView() {
        super( CommonImages.INSTANCE.edit(),
               CommonConstants.INSTANCE.CopyPopupTitle() );
        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        nameTextBox.setTitle( CommonConstants.INSTANCE.NewName() );
        nameTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.NewNameColon(),
                      nameTextBox );

        checkInCommentTextBox.setTitle( CommonConstants.INSTANCE.CheckInComment() );
        checkInCommentTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.CheckInCommentColon(),
                      checkInCommentTextBox );
        hide();

        GenericModalFooter footer = new GenericModalFooter();
        this.copy = footer.addButton( CommonConstants.INSTANCE.CopyPopupCreateACopy(),
                                      IconType.SAVE,
                                      ButtonType.PRIMARY );
        this.cancel = footer.addButton( CommonConstants.INSTANCE.Cancel(),
                                        ButtonType.DEFAULT );
        add( footer );
    }

    @Override
    public HasText getNewName() {
        return nameTextBox;
    }

    @Override
    public HasText getCheckInComment() {
        return checkInCommentTextBox;
    }

    @Override
    public HasClickHandlers getCopyButton() {
        return copy;
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancel;
    }

    @Override
    public void handleInvalidFileName( String baseFileName ) {
        Window.alert( CommonConstants.INSTANCE.InvalidFileName0( baseFileName ) );
    }
}
