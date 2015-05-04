package org.uberfire.ext.editor.commons.client.file;

import org.uberfire.client.mvp.UberView;

public interface CopyPopupUberView extends UberView<CopyPopupUberView.Presenter> {

    public interface Presenter {

        void onCancel();

        void onCopy();
    }

    String getNewName();

    String getCheckInComment();

    void handleInvalidFileName( String baseFileName );

    void show();

    void hide();

}
