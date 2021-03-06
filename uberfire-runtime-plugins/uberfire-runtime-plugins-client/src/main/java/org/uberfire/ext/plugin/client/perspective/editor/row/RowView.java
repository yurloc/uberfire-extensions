package org.uberfire.ext.plugin.client.perspective.editor.row;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.plugin.client.perspective.editor.components.HTMLView;
import org.uberfire.ext.plugin.client.perspective.editor.components.ScreenView;
import org.uberfire.ext.plugin.client.perspective.editor.dnd.DropColumnPanel;
import org.uberfire.ext.plugin.client.perspective.editor.structure.ColumnEditorUI;
import org.uberfire.ext.plugin.client.perspective.editor.structure.EditorWidget;
import org.uberfire.ext.plugin.client.perspective.editor.structure.PerspectiveEditorUI;
import org.uberfire.ext.plugin.client.perspective.editor.structure.RowEditorWidgetUI;
import org.uberfire.ext.plugin.editor.ColumnEditor;
import org.uberfire.ext.plugin.editor.HTMLEditor;
import org.uberfire.ext.plugin.editor.RowEditor;
import org.uberfire.ext.plugin.editor.ScreenEditor;

public class RowView extends Composite {

    private DropColumnPanel oldDropColumnPanel;
    private RowEditorWidgetUI row;

    @UiField
    FluidContainer fluidContainer;

    private EditorWidget editorWidget;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, RowView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public RowView( PerspectiveEditorUI parent ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, "12" );
        build();
    }

    public RowView( PerspectiveEditorUI parent,
                    String rowSpamString ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowSpamString );
        build();
    }

    public RowView( ColumnEditorUI parent,
                    String rowSpamString,
                    DropColumnPanel oldDropColumnPanel ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.oldDropColumnPanel = oldDropColumnPanel;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowSpamString );
        build();

    }

    private RowView( ColumnEditorUI parent,
                     List<String> rowSpans,
                     DropColumnPanel oldDropColumnPanel,
                     RowEditor rowEditor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.oldDropColumnPanel = oldDropColumnPanel;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowSpans );
        reload( rowEditor.getColumnEditors() );
    }

    public RowView( PerspectiveEditorUI parent,
                    RowEditor rowEditor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new RowEditorWidgetUI( parent, fluidContainer, rowEditor.getRowSpam() );
        reload( rowEditor.getColumnEditors() );
    }

    private FluidRow generateColumns( List<ColumnEditor> columnEditors ) {
        FluidRow rowWidget = new FluidRow();
        rowWidget.getElement().getStyle().setProperty( "marginBottom", "15px" );

        for ( ColumnEditor columnEditor : columnEditors ) {

            Column column = createColumn( columnEditor );
            ColumnEditorUI parent = new ColumnEditorUI( row, column, columnEditor.getSpan() );

            // Create the drop panel always, but don't add it to the column in case we're reloading an existing layout, and the column already contains elements
            DropColumnPanel dropColumnPanel = generateDropColumnPanel( column, parent, !columnEditor.hasElements() );

            for ( RowEditor editor : columnEditor.getRows() ) {
                column.add( new RowView( parent, editor.getRowSpam(), dropColumnPanel, editor ) );
            }
            for ( ScreenEditor editor : columnEditor.getScreens() ) {
                column.add( new ScreenView( parent, editor ) );
            }
            for ( HTMLEditor editor : columnEditor.getHtmls() ) {
                column.add( new HTMLView( parent, editor.getHtmlCode() ) );
            }
            rowWidget.add( column );
        }
        return rowWidget;
    }

    private void reload( List<ColumnEditor> columnEditors ) {
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add( generateColumns( columnEditors ) );
    }

    private FluidRow generateColumns() {
        FluidRow rowWidget = new FluidRow();
        rowWidget.getElement().getStyle().setProperty( "marginBottom", "15px" );
        for ( String span : row.getRowSpans() ) {
            Column column = createColumn( span );
            rowWidget.add( column );
        }
        return rowWidget;
    }

    private void build() {
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add( generateColumns() );
    }

    private Column createColumn( ColumnEditor columnEditor ) {
        Column column = new Column( Integer.valueOf( columnEditor.getSpan() ) );
        column.add( generateLabel( "Column" ) );
        setCSS( column );
        return column;
    }

    private DropColumnPanel generateDropColumnPanel( Column column,
                                                     ColumnEditorUI parent,
                                                     boolean addToColumn ) {
        final DropColumnPanel drop = new DropColumnPanel( parent );
        if ( addToColumn ) column.add( drop );
        return drop;
    }

    private Column createColumn( String span ) {
        Column column = new Column( Integer.valueOf( span ) );
        column.add( generateLabel( "Column" ) );
        ColumnEditorUI columnEditor = new ColumnEditorUI( row, column, span );
        column.add( new DropColumnPanel( columnEditor ) );
        setCSS( column );
        return column;
    }

    private void setCSS( Column column ) {
        column.getElement().getStyle().setProperty( "border", "1px solid #DDDDDD" );
        column.getElement().getStyle().setProperty( "backgroundColor", "White" );
    }

    private FluidRow generateHeaderRow() {
        FluidRow row = new FluidRow();
        row.add( generateRowLabelColumn() );
        row.add( generateButtonColumn() );
        return row;
    }

    private Column generateRowLabelColumn() {
        Column column = new Column( 6 );
        Label row1 = generateLabel( "Row" );
        column.add( row1 );
        return column;
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column( 6 );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "right" );
        Button remove = generateButton();
        buttonColumn.add( remove );
        return buttonColumn;
    }

    private Button generateButton() {
        Button remove = new Button( "Remove" );
        remove.setSize( ButtonSize.MINI );
        remove.setType( ButtonType.DANGER );
        remove.setIcon( IconType.REMOVE );
        remove.getElement().getStyle().setProperty( "marginRight", "3px" );
        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                editorWidget.getWidget().remove( RowView.this );
                if ( parentIsAColumn() ) {
                    attachDropColumnPanel();
                }
                row.removeFromParent();
            }
        } );
        return remove;
    }

    private void attachDropColumnPanel() {
        editorWidget.getWidget().add( oldDropColumnPanel );
    }

    private boolean parentIsAColumn() {
        return oldDropColumnPanel != null;
    }

    private Label generateLabel( String row ) {
        Label label = new Label( row );
        label.getElement().getStyle().setProperty( "marginLeft", "3px" );
        return label;
    }

}
