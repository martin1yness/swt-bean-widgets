package com.magnetstreet.swt.beanwidget.datatreegrid;

import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import com.magnetstreet.swt.beanwidget.datagrid2.header.ColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datatreegrid.sorter.DataTreeGridSorter;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * AbstractDataTreeGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/11/11
 */
public abstract class AbstractDataTreeGrid<T extends Comparable<T>> extends Composite implements DataTreeGrid<T> {
    private Logger logger = Logger.getLogger(AbstractDataTreeGrid.class.getSimpleName());

    private boolean initialized = false;
    protected TreeViewer treeViewer;

    protected DataTreeGridSorter treeViewerSorter = new DataTreeGridSorter() {
        @Override public int compare(Viewer viewer, Object e1, Object e2) {
            if(sortingDefinitions.containsKey(((TreeNode)e1).getValue().getClass())) {
                if(sortingDefinitions.get(((TreeNode)e1).getValue().getClass()).containsKey(identifier)) {
                    int asc = sortingDefinitions.get(((TreeNode)e1).getValue().getClass()).get(identifier).compare((T)((TreeNode)e1).getValue(), (T)((TreeNode)e2).getValue());
                    return (direction) ?  asc : -1 * asc ;
                }
            }
            return 0;
        }
    };

    protected Map<String, ColumnHeaderProvider> columnHeaderDefinitions = new LinkedHashMap<String, ColumnHeaderProvider>();
    protected Map<Class, Map<String, ColumnLabelProvider>> columnDefinitions = new HashMap<Class, Map<String, ColumnLabelProvider>>();
    protected Map<Class, Map<String, ColumnFilter>> filterDefinitions = new HashMap<Class, Map<String, ColumnFilter>>();
    protected Map<Class, Map<String, EditingSupport>> cellEditorDefinitions = new HashMap<Class, Map<String, EditingSupport>>();
    protected Map<Class, Map<String, ICellEditorValidator>> cellEditorValidatorDefinitions = new LinkedHashMap<Class, Map<String, ICellEditorValidator>>();
    protected Map<Class, Map<String, Comparator>> sortingDefinitions = new HashMap<Class, Map<String, Comparator>>();

    protected SortedSet<T> beans = new TreeSet<T>(new Comparator<T>() {
        @Override public int compare(T o1, T o2) {
            Comparator<T> comparator = getSortingComparator();
            if(comparator==null)
                return o1.compareTo(o2);
            return comparator.compare(o1, o2);
        }
    });

    public AbstractDataTreeGrid(Composite composite, int i) {
        super(composite, SWT.NONE);
        setLayout(new FillLayout());
        treeViewer = new TreeViewer(this, SWT.FULL_SELECTION|i);
        preInit();
        initialize();
    }

    /**
     * Method invoked before the class is initialized after instantiation. All pre-creation tasks must be completed
     * in this method or an Exception will likely be encountered.
     */
    protected abstract void preInit();

    protected void initialize() {
        buildColumns();
        treeViewer.getTree().setHeaderVisible(true);
        treeViewer.getTree().setLinesVisible(true);
        treeViewer.setContentProvider(new TreeNodeContentProvider() {
            // Override if necessary
        });
        treeViewer.setSorter(treeViewerSorter);
        initialized = true;
    }

    private void buildColumns() {
        for(final String columnIdentifier: columnHeaderDefinitions.keySet()) {
            ColumnHeaderProvider columnHeaderProvider = columnHeaderDefinitions.get(columnIdentifier);
            TreeViewerColumn columnViewer = new TreeViewerColumn(treeViewer, SWT.NONE);
            TreeColumn column = columnViewer.getColumn();
            column.setText(columnHeaderProvider.getTitle());
            column.setWidth(columnHeaderProvider.getWidth());
            if(columnHeaderProvider.getTooltip()!=null) column.setToolTipText(columnHeaderProvider.getTooltip());
            if(columnHeaderProvider.getImage()!=null) column.setImage(columnHeaderProvider.getImage());
            column.addSelectionListener(new SelectionAdapter() {
                @Override public void widgetSelected(SelectionEvent selectionEvent) {
                    treeViewerSorter.setIdentifier(columnIdentifier);
                    treeViewer.refresh();
                }
            });
            columnViewer.setLabelProvider(new ColumnLabelProvider() {
                @Override public String getText(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getText(((TreeNode)element).getValue());
                }
                @Override public Color getBackground(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getBackground(((TreeNode)element).getValue());
                }
                @Override public Font getFont(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getFont(((TreeNode)element).getValue());
                }
                @Override public Color getForeground(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getForeground(((TreeNode)element).getValue());
                }
                @Override public Image getImage(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getImage(((TreeNode)element).getValue());
                }
                @Override public Image getToolTipImage(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipImage(((TreeNode)element).getValue());
                }
                @Override public String getToolTipText(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipText(((TreeNode)element).getValue());
                }
                @Override public Color getToolTipBackgroundColor(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipBackgroundColor(((TreeNode)element).getValue());
                }
                @Override public Color getToolTipForegroundColor(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipForegroundColor(((TreeNode)element).getValue());
                }
                @Override public Font getToolTipFont(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipFont(((TreeNode)element).getValue());
                }
                @Override public Point getToolTipShift(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipShift(((TreeNode)element).getValue());
                }
                @Override public boolean useNativeToolTip(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).useNativeToolTip(((TreeNode)element).getValue());
                }
                @Override public int getToolTipTimeDisplayed(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipTimeDisplayed(((TreeNode)element).getValue());
                }
                @Override public int getToolTipDisplayDelayTime(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipDisplayDelayTime(((TreeNode)element).getValue());
                }
                @Override public int getToolTipStyle(Object element) {
                    return columnDefinitions.get(((TreeNode)element).getValue().getClass()).get(columnIdentifier).getToolTipStyle(((TreeNode)element).getValue());
                }
            });
        }
    }

    abstract protected TreeNode createTreeNode(T bean);

    private TreeNode[] generateTreeNodes() {
        TreeNode[] nodes = new TreeNode[beans.size()];
        int i=0;
        for(T bean: beans) {
            nodes[i++] = createTreeNode(bean);
        }
        return nodes;
    }

    private Comparator<T> getSortingComparator() {
        return null; // todo @todo TODO
    }

    protected void bindHeader(String columnIdentifier, ColumnHeaderProvider headerProvider) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        columnHeaderDefinitions.put(columnIdentifier, headerProvider);
    }

    protected <V> void bindViewer(Class<V> type, String columnIdentifier, ColumnLabelProvider columnLabelProvider) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        if(!columnDefinitions.containsKey(type))
            columnDefinitions.put(type, new LinkedHashMap<String, ColumnLabelProvider>());
        columnDefinitions.get(type).put(columnIdentifier, columnLabelProvider);
    }

    protected <V> void bindSorter(Class<V> type, String columnIdentifier, Comparator<V> comparator) {
        if(!sortingDefinitions.containsKey(type))
            sortingDefinitions.put(type, new LinkedHashMap<String, Comparator>());
        sortingDefinitions.get(type).put(columnIdentifier, comparator);
    }

    @Override public Collection<?> getSelectedBeans() {
        List<TreeNode> nodes = ((TreeSelection) treeViewer.getSelection()).toList();
        Collection modelObjects = new ArrayList(nodes.size());
        for(TreeNode node: nodes) {
            modelObjects.add(node.getValue());
        }
        return modelObjects;
    }
    @Override public void refresh() {
        treeViewer.setInput(generateTreeNodes());
        treeViewer.refresh();
    }
    @Override public void setBeans(Collection<T> beans) {
        this.beans.clear();
        this.beans.addAll(beans);
    }
    @Override public void addBean(T bean) {
        this.beans.add(bean);
    }
    @Override public void removeBean(T bean) {
        this.beans.remove(bean);
    }
    @Override public void removeAllBeans() {
        this.beans.clear();
    }
}
