/*
 * Copyright 2014 JBoss Inc
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

package org.uberfire.ext.widgets.common.client.tables.popup;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.resources.CommonImages;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class NewFilterPopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, NewFilterPopup> {

    }

    public static String FILTER_NAME_PARAM = "filterName";

    @UiField
    public TabPanel tabPanel;

    @UiField
    public Form horizontalForm;

    @UiField
    public FlowPanel existingFiltersPanel;


    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @Inject
    private Event<NotificationEvent> notification;

    HashMap formValues = new HashMap();

    private GridPreferencesStore gridPreferenceStore;

    private final List<ControlGroup> filterControlGroups = new ArrayList<ControlGroup>();

    private CommonImages images = GWT.create( CommonImages.class );

    Command refreshFiltersCommand;

    protected AsyncDataProvider<DataGridFilterSummary> dataProvider;

    PagedTable<DataGridFilterSummary> existingFiltersGrid = new PagedTable<DataGridFilterSummary>(5);


    private static Binder uiBinder = GWT.create( Binder.class );

    public NewFilterPopup() {
        createProvider();

        initColumns();
        setTitle( CommonConstants.INSTANCE.Filter_Management() );

        add( uiBinder.createAndBindUi( this ) );
        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( CommonConstants.INSTANCE.OK(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, null,
                ButtonType.PRIMARY );

        add( footer );

    }


    public void show( Command addfilterCommand, Command refreshFilters, GridPreferencesStore gridPreferenceStore ) {
        addCreateFilterButton( addfilterCommand );
        this.refreshFiltersCommand = refreshFilters;
        this.gridPreferenceStore = gridPreferenceStore;
        refreshGrid();
        super.show();
    }

    private void okButton() {
        refreshFiltersCommand.execute();
        closePopup();

    }

    public void init() {

        horizontalForm.clear();
        filterControlGroups.clear();

        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( CommonConstants.INSTANCE.Filter_Name() );
        HTML lab = new HTML( "<span style=\"color:red\"> * </span>" + "<span style=\"margin-right:10px\">" + CommonConstants.INSTANCE.Filter_Name() + "</span>" );
        controlLabel.add( lab );

        TextBox fieldTextBox = new TextBox();
        fieldTextBox.setName( FILTER_NAME_PARAM );

        controlGroup.add( controlLabel );
        controlGroup.add( fieldTextBox );

        filterControlGroups.add( controlGroup );
        horizontalForm.add( controlGroup );



        existingFiltersPanel.clear();
        existingFiltersPanel.add( existingFiltersGrid );
        existingFiltersGrid.loadPageSizePreferences();
        existingFiltersGrid.setcolumnPickerButtonVisibe( false );
        existingFiltersGrid.setEmptyTableCaption( CommonConstants.INSTANCE.NoCustomFilterAvailable() );

    }

    public void cleanFormValues( List<ControlGroup> controlGroups ) {
        formValues = new HashMap();
        clearErrorMessages();
        for ( ControlGroup groupControl : controlGroups ) {
            if ( groupControl.getWidget( 1 ) instanceof TextBox ) {
                (( TextBox ) groupControl.getWidget( 1 ) ).setText( "" );
            } else if ( groupControl.getWidget( 1 ) instanceof ListBox ) {
                ListBox listBox = ( ListBox ) groupControl.getWidget( 1 );
                listBox.setSelectedIndex( -1 );


            }
        }
    }


    public void closePopup() {
        cleanFormValues( filterControlGroups );
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();
        String filterName = ( String ) formValues.get( FILTER_NAME_PARAM );
        if ( filterName == null || filterName.trim().length() == 0  ) {
            errorMessages.setText( CommonConstants.INSTANCE.Filter_Must_Have_A_Name() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
            valid = false;
        } else {
            errorMessages.setText( "" );
            errorMessagesGroup.setType( ControlGroupType.NONE );
        }
        return valid;
    }

    public void getFormValues( List<ControlGroup> controlGroups ) {
        formValues = new HashMap();

        for ( ControlGroup groupControl : controlGroups ) {
            if ( groupControl.getWidget( 1 ) instanceof TextBox ) {
                formValues.put( ( ( TextBox ) groupControl.getWidget( 1 ) ).getName(),
                        ( ( TextBox ) groupControl.getWidget( 1 ) ).getValue() );
            } else if ( groupControl.getWidget( 1 ) instanceof ListBox ) {
                ListBox listBox = ( ListBox ) groupControl.getWidget( 1 );

                List<String> selectedValues = new ArrayList<String>();
                for ( int i = 0; i < listBox.getItemCount(); i++ ) {
                    if ( listBox.isItemSelected( i ) ) {
                        selectedValues.add( listBox.getValue( i ) );
                    }
                }

                formValues.put( listBox.getName(), selectedValues );
            }
        }
    }

    private void clearErrorMessages() {
        errorMessages.setText( "" );
    }

    public HashMap getFormValues() {
        return formValues;
    }

    public void addListBoxToFilter( String label, String fieldName, boolean multiselect, HashMap<String, String> listBoxInfo ) {
        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">" + label + "</span>" );
        controlLabel.add( lab );

        ListBox listBox = new ListBox( multiselect );
        if ( listBoxInfo != null ) {
            Set listBoxKeys = listBoxInfo.keySet();
            Iterator it = listBoxKeys.iterator();
            String key;
            while ( it.hasNext() ) {
                key = ( String ) it.next();
                listBox.addItem( listBoxInfo.get( key ), key );
            }
        }
        listBox.setName( fieldName );

        controlGroup.add( controlLabel );
        controlGroup.add( listBox );

        filterControlGroups.add( controlGroup );
        horizontalForm.add( controlGroup );
    }

    public void addTextBoxToFilter( String label, String fieldName ) {
        addTextBoxToFilter( label, fieldName, "" );
    }

    public void addTextBoxToFilter( String label, String fieldName, String defaultValue ) {
        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">" + label + "</span>" );
        controlLabel.add( lab );

        TextBox textBox = new TextBox();
        textBox.setName( fieldName );
        if ( defaultValue != null && defaultValue.trim().length() > 0 ) {
            textBox.setText( defaultValue );
        }

        controlGroup.add( controlLabel );
        controlGroup.add( textBox );

        filterControlGroups.add( controlGroup );
        horizontalForm.add( controlGroup );
    }


    private void createProvider() {
        dataProvider = new AsyncDataProvider<DataGridFilterSummary>() {

            @Override
            protected void onRangeChanged( HasData<DataGridFilterSummary> display ) {

                final Range visibleRange = display.getVisibleRange();
                List<DataGridFilterSummary> currentCustomFilters = getData();
                dataProvider.updateRowCount( currentCustomFilters.size(),
                        true );
                int endRange;
                if ( visibleRange.getStart() + 5 < currentCustomFilters.size()) {

                    endRange = visibleRange.getStart() + 5 ;
                } else {
                    endRange = currentCustomFilters.size();
                }
                dataProvider.updateRowData( visibleRange.getStart(),
                        currentCustomFilters.subList( visibleRange.getStart(), endRange ) );

            }
        };
        existingFiltersGrid.setDataProvider( dataProvider );
    }

    private List<DataGridFilterSummary> getData() {
        List<DataGridFilterSummary> customFilters = new ArrayList<DataGridFilterSummary>();
        if ( gridPreferenceStore != null ) {
            final HashMap storedCustomFilters = gridPreferenceStore.getCustomFilters();
            if ( storedCustomFilters != null && storedCustomFilters.size() > 0 ) {
                Set customFilterKeys = storedCustomFilters.keySet();
                Iterator it = customFilterKeys.iterator();
                while ( it.hasNext() ) {

                    final String customFilterName = ( String ) it.next();
                    customFilters.add( new DataGridFilterSummary( customFilterName ) );
                }
            }
        }
        return customFilters;
    }


    public void initColumns() {

        com.google.gwt.user.cellview.client.Column descriptionColumn = initDescriptionColumn();
        com.google.gwt.user.cellview.client.Column actionsColumn = initActionsColumn();

        List<ColumnMeta<DataGridFilterSummary>> columnMetas = new ArrayList<ColumnMeta<DataGridFilterSummary>>();

        columnMetas.add( new ColumnMeta<DataGridFilterSummary>( descriptionColumn, CommonConstants.INSTANCE.Filter_Name() ) );
        columnMetas.add( new ColumnMeta<DataGridFilterSummary>( actionsColumn, CommonConstants.INSTANCE.Actions() ) );
        existingFiltersGrid.addColumns( columnMetas );

    }

    private com.google.gwt.user.cellview.client.Column initDescriptionColumn() {
        // start time
        com.google.gwt.user.cellview.client.Column<DataGridFilterSummary, String> descriptionColumn = new com.google.gwt.user.cellview.client.Column<DataGridFilterSummary, String>( new TextCell() ) {
            @Override
            public String getValue( DataGridFilterSummary object ) {
                return object.getFilterName();
            }
        };
        descriptionColumn.setSortable( true );
        descriptionColumn.setDataStoreName( "log.filterName" );
        return descriptionColumn;
    }

    private com.google.gwt.user.cellview.client.Column initActionsColumn() {
        List<HasCell<DataGridFilterSummary, ?>> cells = new LinkedList<HasCell<DataGridFilterSummary, ?>>();

        cells.add( new RemoveActionHasCell( "Remove", new ActionCell.Delegate<DataGridFilterSummary>() {
            @Override
            public void execute( DataGridFilterSummary filter ) {
                gridPreferenceStore.removeCustomFilter( filter.getFilterName() );
                refreshGrid();
            }
        } ) );


        CompositeCell<DataGridFilterSummary> cell = new CompositeCell<DataGridFilterSummary>( cells );
        com.google.gwt.user.cellview.client.Column<DataGridFilterSummary, DataGridFilterSummary> actionsColumn = new com.google.gwt.user.cellview.client.Column<DataGridFilterSummary, DataGridFilterSummary>(
                cell ) {
            @Override
            public DataGridFilterSummary getValue( DataGridFilterSummary object ) {
                return object;
            }
        };
        return actionsColumn;

    }

    private class RemoveActionHasCell implements HasCell<DataGridFilterSummary, DataGridFilterSummary> {

        private ActionCell<DataGridFilterSummary> cell;

        public RemoveActionHasCell( String text,
                                    ActionCell.Delegate<DataGridFilterSummary> delegate ) {
            cell = new ActionCell<DataGridFilterSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    final DataGridFilterSummary value,
                                    SafeHtmlBuilder sb ) {
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.close() );
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant( "<span title='"+CommonConstants.INSTANCE.RemoveFilter() +" " + value.getFilterName() + "' style='margin-right:5px;'>" );
                    mysb.append( imageProto.getSafeHtml() );
                    mysb.appendHtmlConstant( "</span>" );
                    sb.append( mysb.toSafeHtml() );

                }
            };
        }

        @Override
        public Cell<DataGridFilterSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<DataGridFilterSummary, DataGridFilterSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public DataGridFilterSummary getValue( DataGridFilterSummary object ) {
            return object;
        }
    }


    public void refreshGrid() {
        HasData<DataGridFilterSummary> next = dataProvider.getDataDisplays().iterator().next();
        next.setVisibleRangeAndClearData( next.getVisibleRange(), true );
    }
    private void addCreateFilterButton(final Command addfilterCommand){
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setWidth( "100%" );
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        Button  createFilterButton = new Button(  );
        createFilterButton.setText( CommonConstants.INSTANCE.Add_New_Filter() );

        createFilterButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                getFormValues( filterControlGroups );
                if ( validateForm() ) {
                    addfilterCommand.execute();
                    refreshGrid();
                    cleanFormValues(filterControlGroups);
                    tabPanel.selectTab( 1 );
                }
            }
        } );
        buttonPanel.add( createFilterButton );
        horizontalForm.add( buttonPanel );
    }

}
