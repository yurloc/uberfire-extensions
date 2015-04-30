/*
 * Copyright 2005 JBoss Inc
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

import com.github.gwtbootstrap.client.ui.base.HasVisibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class CopyPopup {

    public interface View extends HasVisibility {

        HasText getNewName();

        HasText getCheckInComment();

        HasClickHandlers getCopyButton();

        HasClickHandlers getCancelButton();

        void handleInvalidFileName( String baseFileName );
    }

    private final View view;
    private final Path path;
    private final Validator validator;
    private final CommandWithFileNameAndCommitMessage command;

    public CopyPopup( Path path, CommandWithFileNameAndCommitMessage command, View view ) {
        this( path,
              new Validator() {
                  @Override
                  public void validate( final String value,
                                        final ValidatorCallback callback ) {
                      callback.onSuccess();
                  }
              },
              command, view );
    }

    public CopyPopup( Path path, Validator validator, CommandWithFileNameAndCommitMessage command, View view ) {
        this.validator = checkNotNull( "validator",
                                       validator );
        this.path = checkNotNull( "path",
                                  path );
        this.command = checkNotNull( "command",
                                     command );
        this.view = checkNotNull( "view",
                                  view );
        this.view.getCopyButton().addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                copy();
            }
        } );
        this.view.getCancelButton().addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                hide();
            }
        } );
    }

    public void show() {
        view.show();
    }

    private void hide() {
        view.hide();
    }

    void copy() {
        final String baseFileName = view.getNewName().getText();
        final String originalFileName = path.getFileName();
        final String extension = ( originalFileName.lastIndexOf( "." ) > 0
                ? originalFileName.substring( originalFileName.lastIndexOf( "." ) ) : "" );
        final String fileName = baseFileName + extension;

        validator.validate( fileName,
                            new ValidatorCallback() {
                                @Override
                                public void onSuccess() {
                                    hide();
                                    command.execute( new FileNameAndCommitMessage( baseFileName,
                                                                                   view.getCheckInComment().getText() ) );
                                }

                                @Override
                                public void onFailure() {
                                    view.handleInvalidFileName( baseFileName );
                                }
                            } );
    }
}
