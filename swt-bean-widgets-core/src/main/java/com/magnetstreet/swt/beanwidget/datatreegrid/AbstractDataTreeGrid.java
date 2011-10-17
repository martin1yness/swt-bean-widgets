package com.magnetstreet.swt.beanwidget.datatreegrid;

import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import com.magnetstreet.swt.beanwidget.datagrid2.header.ColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datatreegrid.contextmenu.ContextMenuAction;
import com.magnetstreet.swt.beanwidget.datatreegrid.contextmenu.ContextMenuManager;
import com.magnetstreet.swt.beanwidget.datatreegrid.sorter.DataTreeGridSorter;
import com.magnetstreet.swt.util.BeanUtil;
import com.magnetstreet.swt.util.SWTUtil;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
    protected Map<Class, List<ColumnFilter>> filterDefinitions = new HashMap<Class, List<ColumnFilter>>();
    protected Map<Class, Map<String, EditingSupport>> cellEditorDefinitions = new HashMap<Class, Map<String, EditingSupport>>();
    protected Map<Class, Map<String, ICellEditorValidator>> cellEditorValidatorDefinitions = new LinkedHashMap<Class, Map<String, ICellEditorValidator>>();
    protected Map<Class, Map<String, Comparator>> sortingDefinitions = new HashMap<Class, Map<String, Comparator>>();

    protected ContextMenuManager contextMenuManager = null;
    protected List<ContextMenuAction> contextMenuActions = new LinkedList<ContextMenuAction>();

    protected SortedSet<T> beans = new TreeSet<T>();

    protected ViewerFilter defaultViewerFilter = new ViewerFilter() {
        @Override public boolean select(Viewer viewer, Object parentElement, Object element) {
            if(filterDefinitions.containsKey(((TreeNode)element).getValue().getClass())) {
                for(ColumnFilter filter: filterDefinitions.get(((TreeNode)element).getValue().getClass())) {
                    if(!filter.checkModelProperty(((TreeNode)element).getValue()))
                        return false;
                }
            }
            return true;
        }
        @Override public boolean isFilterProperty(Object element, String property) { return true; }
    };

    public AbstractDataTreeGrid(Composite composite, int i) {
        super(composite, SWT.NONE);
        setLayout(new FillLayout());
        if(SWTUtil.hasStyle(i, SWT.CHECK))
            treeViewer = new CheckboxTreeViewer(this, SWT.FULL_SELECTION|i);
        else
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
        treeViewer.addFilter(defaultViewerFilter);
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
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getText(((TreeNode) element).getValue());
                    return "";
                }
                @Override public Color getBackground(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getBackground(((TreeNode) element).getValue());
                    return super.getBackground(element);
                }
                @Override public Font getFont(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getFont(((TreeNode) element).getValue());
                    return super.getFont(element);
                }
                @Override public Color getForeground(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getForeground(((TreeNode) element).getValue());
                    return super.getForeground(element);
                }
                @Override public Image getImage(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getImage(((TreeNode) element).getValue());
                    return super.getImage(element);
                }
                @Override public Image getToolTipImage(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipImage(((TreeNode) element).getValue());
                    return super.getToolTipImage(element);
                }
                @Override public String getToolTipText(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipText(((TreeNode) element).getValue());
                    return super.getToolTipText(element);
                }
                @Override public Color getToolTipBackgroundColor(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipBackgroundColor(((TreeNode) element).getValue());
                    return super.getToolTipBackgroundColor(element);
                }
                @Override public Color getToolTipForegroundColor(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipForegroundColor(((TreeNode) element).getValue());
                    return super.getToolTipForegroundColor(element);
                }
                @Override public Font getToolTipFont(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipFont(((TreeNode) element).getValue());
                    return super.getToolTipFont(element);
                }
                @Override public Point getToolTipShift(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipShift(((TreeNode) element).getValue());
                    return super.getToolTipShift(element);
                }
                @Override public boolean useNativeToolTip(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).useNativeToolTip(((TreeNode) element).getValue());
                    return super.useNativeToolTip(element);
                }
                @Override public int getToolTipTimeDisplayed(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipTimeDisplayed(((TreeNode) element).getValue());
                    return super.getToolTipTimeDisplayed(element);
                }
                @Override public int getToolTipDisplayDelayTime(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipDisplayDelayTime(((TreeNode) element).getValue());
                    return super.getToolTipDisplayDelayTime(element);
                }
                @Override public int getToolTipStyle(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipStyle(((TreeNode) element).getValue());
                    return super.getToolTipStyle(element);
                }
            });
        }
    }

    private void addContextMenu() {
        if(contextMenuManager==null)
            contextMenuManager = new ContextMenuManager(treeViewer);
        contextMenuManager.setRemoveAllWhenShown(true);
        contextMenuManager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                Collection selectedBeans = getSelectedBeans(Object.class);
                contextMenuManager.setSelectedContextModel(selectedBeans);
                for(final ContextMenuAction action: contextMenuActions) {
                    contextMenuManager.add(new ContextMenuAction() {
                        @Override public String getText() {
                            return action.getText();
                        }
                        @Override public void setViewer(Viewer viewer) {
                            action.setViewer(viewer);
                            super.setViewer(viewer);    //To change body of overridden methods use File | Settings | File Templates.
                        }
                        @Override public void setSelectedContextModel(Collection selectedContextModel) {
                            action.setSelectedContextModel(selectedContextModel);
                            super.setSelectedContextModel(selectedContextModel);    //To change body of overridden methods use File | Settings | File Templates.
                        }
                        @Override public void run() {
                            action.run();
                        }
                        @Override public void runWithEvent(Event event) {
                            action.runWithEvent(event);
                        }
                    });
                }
            }
        });
        treeViewer.getControl().setMenu(contextMenuManager.createContextMenu(treeViewer.getControl()));
    }

    abstract protected TreeNode createTreeNode(T bean);

    protected TreeNode[] recursiveGenerateChildrenTreeNodes(TreeNode parent, String propertyChain) {
        Object nextCollection = BeanUtil.getFieldValueWithGetter(parent.getValue(), (propertyChain.indexOf('.') == -1) ? propertyChain : propertyChain.substring(0, propertyChain.indexOf('.')));
        if(nextCollection instanceof Collection) {
            TreeNode[] children = new TreeNode[((Collection) nextCollection).size()];
            int i=0;
            for(Object obj: (Collection)nextCollection) {
                children[i] = new TreeNode(obj);
                children[i].setParent(parent);
                if(propertyChain.indexOf('.')>0)
                    children[i].setChildren(recursiveGenerateChildrenTreeNodes(children[i], propertyChain.substring(propertyChain.indexOf('.'))));
                i++;
            }
            return children;
        }
        return null;
    }

    private TreeNode[] generateTreeNodes() {
        TreeNode[] nodes = new TreeNode[beans.size()];
        int i=0;
        for(T bean: beans) {
            nodes[i++] = createTreeNode(bean);
        }
        return nodes;
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        treeViewer.addDoubleClickListener(listener);
    }

    public TreeViewer getTreeViewer() { return treeViewer; }

    public <V> void bindFilter(Class<V> type, ColumnFilter<V> columnFilter) {
        if(!filterDefinitions.containsKey(type))
            filterDefinitions.put(type, new LinkedList<ColumnFilter>());
        filterDefinitions.get(type).add(columnFilter);
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

    protected <V> void bindContextMenuAction(ContextMenuAction action) {
        if(contextMenuManager==null)
            addContextMenu();
        contextMenuActions.add(action);
    }

    @Override public Collection<T> getCheckedRootBeans() {
        if( !SWTUtil.hasStyle(treeViewer.getTree().getStyle(), SWT.CHECK) )
            throw new RuntimeException("DataTreeGrid was not initialized with the SWT.CHECK style, there are no checked beans.");
        Collection<T> modelObjects = new ArrayList<T>();
        Object[] treeNodes = ((CheckboxTreeViewer)treeViewer).getCheckedElements();
        for(Object treeNode: treeNodes) {
            modelObjects.add((T)getRootNode((TreeNode)treeNode).getValue());
        }
        return modelObjects;
    }
    @Override public <V> Collection<V> getCheckedBeans(Class<V> type) {
        if( !SWTUtil.hasStyle(treeViewer.getTree().getStyle(), SWT.CHECK) )
            throw new RuntimeException("DataTreeGrid was not initialized with the SWT.CHECK style, there are no checked beans.");
        Collection<V> modelObjects = new ArrayList<V>();
        Object[] treeNodes = ((CheckboxTreeViewer)treeViewer).getCheckedElements();
        for(Object node: treeNodes) {
            if(type.isAssignableFrom(node.getClass()))
                modelObjects.add((V)((TreeNode)node).getValue());
        }
        return modelObjects;
    }
    @Override public Collection<T> getSelectedRootBeans() {
        List<TreeNode> nodes = ((TreeSelection) treeViewer.getSelection()).toList();
        Collection<T> modelObjects = new ArrayList<T>();
        for(TreeNode node: nodes) {
            modelObjects.add((T)getRootNode(node).getValue());
        }
        return modelObjects;
    }
    @Override public <V> Collection<V> getSelectedBeans(Class<V> type) {
        List<TreeNode> nodes = ((TreeSelection) treeViewer.getSelection()).toList();
        Collection<V> modelObjects = new ArrayList<V>(nodes.size());
        for(TreeNode node: nodes) {
            if(type.isAssignableFrom(node.getClass()))
                modelObjects.add((V)node.getValue());
        }
        return modelObjects;
    }

    private TreeNode getRootNode(TreeNode node) {
        if(node.getParent()==null)
            return node;
        return getRootNode(node.getParent());
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
