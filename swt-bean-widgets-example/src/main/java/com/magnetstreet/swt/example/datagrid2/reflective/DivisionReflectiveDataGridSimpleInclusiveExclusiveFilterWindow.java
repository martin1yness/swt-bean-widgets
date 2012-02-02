package com.magnetstreet.swt.example.datagrid2.reflective;

import com.magnetstreet.swt.beanwidget.datagrid2.filter.SimpleInclusiveExclusiveKeywordColumnFilter;
import com.magnetstreet.swt.example.bean.Division;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.HashSet;
import java.util.Set;

/**
 * DivisionReflectiveDataGridSimpleInclusiveExclusiveFilterWindow
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 9/21/11
 */
public class DivisionReflectiveDataGridSimpleInclusiveExclusiveFilterWindow extends ApplicationWindow {
    protected Group filtersGroup;
    protected CLabel descriptionFilterCLabel;
    protected Text descriptionFilterText;
    protected DivisionReflectiveDataTableGrid dataGrid;

    protected Set<Division> divisions = new HashSet<Division>();

    public DivisionReflectiveDataGridSimpleInclusiveExclusiveFilterWindow(Shell parentShell) {
        super(parentShell);
        Division dA = new Division();
        dA.setId(100);
        dA.setName("DivisionA");
        dA.setDescription("This is division A the first division in the company.");

        Division dB = new Division();
        dB.setId(200);
        dB.setName("DivisionB");
        dB.setDescription("This is division B the second division in the company.");

        Division dC = new Division();
        dC.setId(300);
        dC.setName("DivisionC");
        dC.setDescription("This is division C the third division in the company.");

        divisions.add(dA);
        divisions.add(dB);
        divisions.add(dC);
    }

    @Override protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.EMBEDDED);
        container.setLayout(new FormLayout());

        filtersGroup = new Group(container, SWT.NONE);
        filtersGroup.setLayout(new FormLayout());
        filtersGroup.setText("Filter(s)");
        FormData filtersGroupLayoutData = new FormData(400,50);
        filtersGroupLayoutData.top = new FormAttachment(0,100,0);
        filtersGroupLayoutData.left = new FormAttachment(0,100,0);
        filtersGroupLayoutData.right = new FormAttachment(100,100,0);
        filtersGroup.setLayoutData(filtersGroupLayoutData);

        descriptionFilterCLabel = new CLabel(filtersGroup, SWT.RIGHT);
        descriptionFilterCLabel.setText("Description:");
        FormData descriptionFilterCLabelLayoutData = new FormData(100,20);
        descriptionFilterCLabelLayoutData.left = new FormAttachment(0,100,5);
        descriptionFilterCLabelLayoutData.top = new FormAttachment(0,100,5);
        descriptionFilterCLabel.setLayoutData(descriptionFilterCLabelLayoutData);

        descriptionFilterText = new Text(filtersGroup, SWT.BORDER);
        FormData descriptionFilterTextLayoutData = new FormData(300,20);
        descriptionFilterTextLayoutData.left = new FormAttachment(descriptionFilterCLabel, 5);
        descriptionFilterTextLayoutData.top = new FormAttachment(0,100,5);
        descriptionFilterText.setLayoutData(descriptionFilterTextLayoutData);

        dataGrid = new DivisionReflectiveDataTableGrid(container, SWT.FULL_SELECTION);
        FormData dataGridLayoutData = new FormData(400,400);
        dataGridLayoutData.left = new FormAttachment(0,100,0);
        dataGridLayoutData.right = new FormAttachment(100,100,0);
        dataGridLayoutData.top = new FormAttachment(filtersGroup, 5);
        dataGridLayoutData.bottom = new FormAttachment(100,100,0);
        dataGrid.setLayoutData(dataGridLayoutData);

        bindFilters();
        bindListeners();

        dataGrid.setBeans(divisions);
        dataGrid.refresh();

        return container;
    }

    private void bindFilters() {
        dataGrid.bindFilter("description", new SimpleInclusiveExclusiveKeywordColumnFilter() {
            @Override protected String getFilterText() {
                return descriptionFilterText.getText();
            }
        });
    }

    private void bindListeners() {
        ModifyListener applyFiltersModifyListener = new ModifyListener() {
            @Override public void modifyText(ModifyEvent modifyEvent) {
                dataGrid.refresh();
            }
        };
        descriptionFilterText.addModifyListener(applyFiltersModifyListener);
    }
}
