package com.magnetstreet.swt.beanwidget.datatreegrid;

import com.google.common.collect.Collections2;
import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import com.magnetstreet.swt.beanwidget.datagrid2.header.ColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datatreegrid.contextmenu.ContextMenuAction;
import com.magnetstreet.swt.beanwidget.datatreegrid.contextmenu.ContextMenuManager;
import com.magnetstreet.swt.beanwidget.datatreegrid.sorter.DataTreeGridSorter;
import com.magnetstreet.swt.exception.InvalidGridStyleException;
import com.magnetstreet.swt.util.BeanUtil;
import com.magnetstreet.swt.util.SWTUtil;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

import java.nio.charset.MalformedInputException;
import java.util.*;
import java.util.List;
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

    protected ViewerFilter defaultViewerFilter = new ViewerFilter() {
        @Override public boolean select(Viewer viewer, Object parentElement, Object element) {
            Class type =((TreeNode) element).getValue().getClass();
            if(filterDefinitions.containsKey(type)) {
                for(ColumnFilter filter: filterDefinitions.get(type).values()) {
                    if(!filter.checkModelProperty(((TreeNode) element).getValue()))
                        return false;
                }
            }
            return true;
        }
        @Override public boolean isFilterProperty(Object element, String property) { return true; }
    };

    protected ColumnLabelProvider defaultLabelProvider = new ColumnHeaderProvider() {
        @Override public String getTitle() { return ""; }
    };

    protected Map<String, ColumnHeaderProvider> columnHeaderDefinitions = new LinkedHashMap<String, ColumnHeaderProvider>();
    protected Map<Class, Map<String, ColumnLabelProvider>> columnDefinitions = new HashMap<Class, Map<String, ColumnLabelProvider>>();
    protected Map<Class, Map<String, ColumnFilter>> filterDefinitions = new HashMap<Class, Map<String, ColumnFilter>>();
    protected Map<Class, Map<String, EditingSupport>> cellEditorDefinitions = new HashMap<Class, Map<String, EditingSupport>>();
    protected Map<Class, Map<String, ICellEditorValidator>> cellEditorValidatorDefinitions = new LinkedHashMap<Class, Map<String, ICellEditorValidator>>();
    protected Map<Class, Map<String, Comparator>> sortingDefinitions = new HashMap<Class, Map<String, Comparator>>();

    protected ContextMenuManager contextMenuManager = null;
    protected List<IContributionItem> contextMenuActions = new LinkedList<IContributionItem>();

    protected List<T> beans = new ArrayList<T>();

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
        getTreeViewer().getTree().setHeaderVisible(true);
        getTreeViewer().getTree().setLinesVisible(true);
        getTreeViewer().setContentProvider(new TreeNodeContentProvider() {
            // Override if necessary
        });
        getTreeViewer().setSorter(treeViewerSorter);
        getTreeViewer().addFilter(defaultViewerFilter);
        if(contextMenuActions!=null && contextMenuActions.size() > 0)
            addContextMenu();
        initialized = true;
    }

    private void buildColumns() {
        for(final String columnIdentifier: columnHeaderDefinitions.keySet()) {
            ColumnHeaderProvider columnHeaderProvider = columnHeaderDefinitions.get(columnIdentifier);
            TreeViewerColumn columnViewer = new TreeViewerColumn(getTreeViewer(), SWT.NONE);
            TreeColumn column = columnViewer.getColumn();
            column.setText(columnHeaderProvider.getTitle());
            column.setWidth(columnHeaderProvider.getWidth());
            if(columnHeaderProvider.getTooltip()!=null) column.setToolTipText(columnHeaderProvider.getTooltip());
            if(columnHeaderProvider.getImage()!=null) column.setImage(columnHeaderProvider.getImage());
            column.addSelectionListener(new SelectionAdapter() {
                @Override public void widgetSelected(SelectionEvent selectionEvent) {
                    treeViewerSorter.setIdentifier(columnIdentifier);
                    getTreeViewer().refresh();
                }
            });
            columnViewer.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getText(((TreeNode) element).getValue());
                    return defaultLabelProvider.getText(((TreeNode) element).getValue());
                }

                @Override
                public Color getBackground(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getBackground(((TreeNode) element).getValue());
                    return defaultLabelProvider.getBackground(((TreeNode) element).getValue());
                }

                @Override
                public Font getFont(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getFont(((TreeNode) element).getValue());
                    return defaultLabelProvider.getFont(((TreeNode) element).getValue());
                }

                @Override
                public Color getForeground(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getForeground(((TreeNode) element).getValue());
                    return defaultLabelProvider.getForeground(((TreeNode) element).getValue());
                }

                @Override
                public Image getImage(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getImage(((TreeNode) element).getValue());
                    return defaultLabelProvider.getImage(((TreeNode) element).getValue());
                }

                @Override
                public Image getToolTipImage(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipImage(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipImage(((TreeNode) element).getValue());
                }

                @Override
                public String getToolTipText(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipText(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipText(((TreeNode) element).getValue());
                }

                @Override
                public Color getToolTipBackgroundColor(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipBackgroundColor(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipBackgroundColor(((TreeNode) element).getValue());
                }

                @Override
                public Color getToolTipForegroundColor(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipForegroundColor(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipForegroundColor(((TreeNode) element).getValue());
                }

                @Override
                public Font getToolTipFont(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipFont(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipFont(((TreeNode) element).getValue());
                }

                @Override
                public Point getToolTipShift(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipShift(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipShift(((TreeNode) element).getValue());
                }

                @Override
                public boolean useNativeToolTip(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).useNativeToolTip(((TreeNode) element).getValue());
                    return defaultLabelProvider.useNativeToolTip(((TreeNode) element).getValue());
                }

                @Override
                public int getToolTipTimeDisplayed(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipTimeDisplayed(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipTimeDisplayed(((TreeNode) element).getValue());
                }

                @Override
                public int getToolTipDisplayDelayTime(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipDisplayDelayTime(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipDisplayDelayTime(((TreeNode) element).getValue());
                }

                @Override
                public int getToolTipStyle(Object element) {
                    if (columnDefinitions.containsKey(((TreeNode) element).getValue().getClass()) && columnDefinitions.get(((TreeNode) element).getValue().getClass()).containsKey(columnIdentifier))
                        return columnDefinitions.get(((TreeNode) element).getValue().getClass()).get(columnIdentifier).getToolTipStyle(((TreeNode) element).getValue());
                    return defaultLabelProvider.getToolTipStyle(((TreeNode) element).getValue());
                }
            });
        }
    }

    public String captureSerializedColumnWidths() {
        StringBuilder sb = new StringBuilder();
        for(TreeColumn col: getTreeViewer().getTree().getColumns()) {
            sb.append(col.getText());
            sb.append('=');
            sb.append(col.getWidth());
            sb.append(';');
        }
        return sb.toString();
    }
    public void applySerializedColumnWidths(String widths) {
        if(widths==null || widths.trim().equals(""))
            return;
        String[] widthsArr = widths.split(";");
        if(widthsArr.length!=getTreeViewer().getTree().getColumnCount())
            throw new RuntimeException("Malformed column widths string: "+ widths);
        Map<String,Integer> widthsTable = new HashMap<String, Integer>();
        for(String widthDef: widthsArr) {
            String[] widthDefArr = widthDef.split("=");
            widthsTable.put(widthDefArr[0], Integer.parseInt(widthDefArr[1]));
        }
        for(TreeColumn col: getTreeViewer().getTree().getColumns()) {
            if(widthsTable.containsKey(col.getText()))
                col.setWidth(widthsTable.get(col.getText()));
        }
    }

    protected void addContextMenu() {
        if(contextMenuManager!=null)
            return;
        contextMenuManager = new ContextMenuManager(getTreeViewer());
        contextMenuManager.setRemoveAllWhenShown(true);
        contextMenuManager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                Collection selectedBeans = getSelectedBeans(Object.class);
                contextMenuManager.setSelectedContextModel(selectedBeans);
                for(IContributionItem contribItem: contextMenuActions) {
                    if(contribItem instanceof ActionContributionItem && ContextMenuAction.class.isAssignableFrom(((ActionContributionItem)contribItem).getAction().getClass())) {
                        for(Object sb: selectedBeans) {
                            ContextMenuAction action = (ContextMenuAction)((ActionContributionItem)contribItem).getAction();
                            if(action.getApplicableTo()==null || action.getApplicableTo().size()==0 || action.getApplicableTo().contains(sb.getClass())) {
                                contextMenuManager.add(action.clone());
                                break;
                            }
                        }
                    } else {
                        contextMenuManager.add(contribItem);
                    }
                }
            }
        });
        getTreeViewer().getControl().setMenu(contextMenuManager.createContextMenu(getTreeViewer().getControl()));
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
                    children[i].setChildren(recursiveGenerateChildrenTreeNodes(children[i], propertyChain.substring(propertyChain.indexOf('.')+1)));
                i++;
            }
            return children;
        } else { // Assume single object
            return new TreeNode[]{new TreeNode(nextCollection)};
        }
    }

    private TreeNode[] generateTreeNodes() {
        TreeNode[] nodes = new TreeNode[getBeans().size()];
        int i=0;
        for(T bean: getBeans()) {
            nodes[i++] = createTreeNode(bean);
        }
        return nodes;
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        getTreeViewer().addDoubleClickListener(listener);
    }

    public TreeViewer getTreeViewer() { return treeViewer; }

    public <V> void bindFilter(Class<V> modelType, String columnIdentifier, ColumnFilter<V> columnFilter) {
        if(!filterDefinitions.containsKey(modelType))
            filterDefinitions.put(modelType, new LinkedHashMap<String, ColumnFilter>());
        filterDefinitions.get(modelType).put(columnIdentifier, columnFilter);
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

    protected <V> void bindContextMenuAction(IContributionItem action) {
        contextMenuActions.add(action);
    }

    @Override public void uncheckAllItems() {
        if( !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.CHECK) )
            throw new InvalidGridStyleException("DataTreeGrid was not initialized with the SWT.CHECK style, there are no checked beans.");
        List<TreeItem> items = recursiveGetTreeItems(true, ((CheckboxTreeViewer) getTreeViewer()).getTree().getItems(), null);
        for(TreeItem item: items)
            item.setChecked(false);
    }
    @Override public void checkAllItems() {
        if( !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.CHECK) )
            throw new InvalidGridStyleException("DataTreeGrid was not initialized with the SWT.CHECK style, there are no checked beans.");
        List<TreeItem> items = recursiveGetTreeItems(true, ((CheckboxTreeViewer) getTreeViewer()).getTree().getItems(), null);
        for(TreeItem item: items)
            item.setChecked(true);
    }
    @Override public void checkBeans(Collection beansToCheck) {
        if( beansToCheck == null || beansToCheck.size()==0)
            return;
        if( !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.CHECK) )
            throw new InvalidGridStyleException("DataTreeGrid was not initialized with the SWT.CHECK style, cannot check beans.");

        List<TreeItem> items = recursiveGetTreeItems(true, ((CheckboxTreeViewer) getTreeViewer()).getTree().getItems(), beansToCheck.toArray());
        for(TreeItem item: items)
            item.setChecked(true);
    }
    @Override public void deselectAllItems() {
        getTreeViewer().setSelection(new StructuredSelection());
    }
    @Override public void selectAllItems() {
        if( !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.MULTI) )
            throw new InvalidGridStyleException("DataTreeGrid was not created with the SWT.MULTI style, cannot select more than one row.");
        List<TreeItem> treeItems = recursiveGetTreeItems(true, getTreeViewer().getTree().getItems(), null);
        TreeNode[] nodes = new TreeNode[treeItems.size()];
        for(int i=0; i<treeItems.size(); i++)
            nodes[i] = (TreeNode)(treeItems.get(i)).getData();
        getTreeViewer().setSelection(new StructuredSelection(nodes));
    }
    @Override public void selectBeans(Collection beansToSelect) {
        if( beansToSelect == null || beansToSelect.size()==0)
            return;
        if( beansToSelect.size() > 1 && !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.MULTI) )
            throw new InvalidGridStyleException("DataTreeGrid was not created with the SWT.MULTI style, cannot select more than one row.");
        Set beansToSelectSet = new HashSet();
        beansToSelectSet.addAll(beansToSelect);
        beansToSelectSet.addAll(getSelectedBeans());
        List<TreeItem> treeItems = recursiveGetTreeItems(true, getTreeViewer().getTree().getItems(), beansToSelectSet.toArray());
        TreeNode[] nodes = new TreeNode[treeItems.size()];
        for(int i=0; i<treeItems.size(); i++)
            nodes[i] = (TreeNode)treeItems.get(i).getData();
        getTreeViewer().setSelection(new StructuredSelection(nodes));
    }
    protected List<TreeItem> recursiveGetTreeItems(boolean visibleOnly, TreeItem[] treeItems, Object...beans) {
        List<TreeItem> items = new ArrayList<TreeItem>();
        for(TreeItem item: treeItems) {
            if(item.getData() instanceof TreeNode) {
                if(beans!=null && beans.length > 0) {
                    for(Object bean: beans) {
                        if(((TreeNode)item.getData()).getValue().getClass() != bean.getClass())
                            continue;
                        if(((Comparable)((TreeNode)item.getData()).getValue()).compareTo(bean) == 0) {
                            items.add(item);
                            break;
                        }
                    }
                } else {
                    items.add(item);
                }
                if(!visibleOnly || item.getExpanded()) {
                    if(item.getItemCount()>0)
                        items.addAll(recursiveGetTreeItems(visibleOnly, item.getItems(), beans));
                }
            }
        }
        return items;
    }


    @Override public Collection<T> getCheckedRootBeans() {
        if( !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.CHECK) )
            throw new InvalidGridStyleException("DataTreeGrid was not initialized with the SWT.CHECK style, there are no checked beans.");
        Collection<T> modelObjects = new ArrayList<T>();
        Object[] treeNodes = ((CheckboxTreeViewer)getTreeViewer()).getCheckedElements();
        for(Object treeNode: treeNodes) {
            modelObjects.add((T)getRootNode((TreeNode)treeNode).getValue());
        }
        return modelObjects;
    }
    @Override public <V> Collection<V> getCheckedBeans(Class<V> type) {
        if( !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.CHECK) )
            throw new InvalidGridStyleException("DataTreeGrid was not initialized with the SWT.CHECK style, there are no checked beans.");
        Object[] treeNodes = ((CheckboxTreeViewer)getTreeViewer()).getCheckedElements();
        Collection<V> modelObjects = new ArrayList<V>(treeNodes.length);
        for(Object node: treeNodes) {
            if(type.isAssignableFrom(((TreeNode)node).getValue().getClass()))
                modelObjects.add((V)((TreeNode)node).getValue());
        }
        return modelObjects;
    }
    @Override public Collection getCheckedBeans() {
        if( !SWTUtil.hasStyle(getTreeViewer().getTree().getStyle(), SWT.CHECK) )
            throw new InvalidGridStyleException("DataTreeGrid was not initialized with the SWT.CHECK style, there are no checked beans.");
        Object[] treeNodes = ((CheckboxTreeViewer)getTreeViewer()).getCheckedElements();
        Collection modelObjects = new ArrayList(treeNodes.length);
        for(Object node: treeNodes)
            modelObjects.add(((TreeNode)node).getValue());
        return modelObjects;
    }
    @Override public Collection<T> getSelectedRootBeans() {
        List<TreeNode> nodes = ((TreeSelection) getTreeViewer().getSelection()).toList();
        Collection<T> modelObjects = new ArrayList<T>();
        for(TreeNode node: nodes) {
            modelObjects.add((T)getRootNode(node).getValue());
        }
        return modelObjects;
    }
    @Override public <V> Collection<V> getSelectedBeans(Class<V> type) {
        List<TreeNode> nodes = ((TreeSelection) getTreeViewer().getSelection()).toList();
        Collection<V> modelObjects = new ArrayList<V>(nodes.size());
        for(TreeNode node: nodes) {
            if(type.isAssignableFrom(node.getValue().getClass()))
                modelObjects.add((V)node.getValue());
        }
        return modelObjects;
    }
    @Override public Collection getSelectedBeans() {
        List<TreeNode> nodes = ((TreeSelection) getTreeViewer().getSelection()).toList();
        Collection modelObjects = new ArrayList(nodes.size());
        for(TreeNode node: nodes) {
            modelObjects.add(node.getValue());
        }

        return modelObjects;
    }

    @Override public Collection getExpandedBeans() {
        List expandedItems = new LinkedList();
        for(TreeItem treeItem: recursiveGetTreeItems(true, getTreeViewer().getTree().getItems())) {
            if(treeItem.getExpanded())
                expandedItems.add(((TreeNode)treeItem.getData()).getValue());
        }
        return expandedItems;
    }
    @Override public void expandBeans(Collection beansToExpand) {
        if(beansToExpand==null || beansToExpand.isEmpty())
            return;
        ArrayList<TreeNode> nodes = new ArrayList<TreeNode>(beansToExpand.size());
        for(TreeItem treeItem: recursiveGetTreeItems(false, getTreeViewer().getTree().getItems(), beansToExpand.toArray(new Object[beansToExpand.size()]))) {
            nodes.add((TreeNode)treeItem.getData());
            TreeItem parent = treeItem.getParentItem();
            while(parent!=null) {
                nodes.add((TreeNode)parent.getData());
                parent = parent.getParentItem();
            }
        }
        getTreeViewer().setExpandedElements(nodes.toArray());
    }

    private TreeNode getRootNode(TreeNode node) {
        if(node.getParent()==null)
            return node;
        return getRootNode(node.getParent());
    }


    @Override public Object getTopBean() {
        TreeItem topItem = getTreeViewer().getTree().getTopItem();
        if(topItem!=null) {
            Object data = topItem.getData();
            if(data instanceof TreeNode)
                return ((TreeNode)data).getValue();
        }
        return null;
    }

    @Override public void setTopBean(Comparable bean) {
        for(TreeItem item: getTreeViewer().getTree().getItems()) {
            if(item.getData() instanceof TreeNode) {
                if(((TreeNode) item.getData()).getValue().getClass() == bean.getClass()) {
                    if(((Comparable)((TreeNode)item.getData()).getValue()).compareTo(bean)==0) {
                        getTreeViewer().getTree().setTopItem(item);
                        break;
                    }
                }
            }
        }
    }


    @Override public void refresh() {
        getTreeViewer().setInput(generateTreeNodes());
        getTreeViewer().refresh();
    }
    @Override public List getBeans(Comparable matcher) {
        List matchedBeans = new ArrayList();
        List<TreeItem> treeItems = recursiveGetTreeItems(false, getTreeViewer().getTree().getItems(), null);
        for(TreeItem treeItem: treeItems) {
            if(matcher.compareTo(((TreeNode)treeItem.getData()).getValue())==0)
                matchedBeans.add(((TreeNode)treeItem.getData()).getValue());
        }
        return matchedBeans;
    }
    @Override public <V> List<V> getBeans(Class<V> type, Comparable<V> matcher) {
        List<V> matchedBeans = new ArrayList<V>();
        List<TreeItem> treeItems = recursiveGetTreeItems(false, getTreeViewer().getTree().getItems(), null);
        for(TreeItem treeItem: treeItems) {
            if(type.isAssignableFrom( ((TreeNode)treeItem.getData()).getValue().getClass() )) {
                if(matcher.compareTo((V)((TreeNode)treeItem.getData()).getValue())==0)
                    matchedBeans.add((V)((TreeNode)treeItem.getData()).getValue());
            }
        }
        return matchedBeans;
    }
    @Override public List<T> getBeans() { return beans; }
    @Override public void setBeans(Collection<T> beans) {
        getBeans().clear();
        getBeans().addAll(beans);
    }
    @Override public void addBean(T bean) {
        getBeans().add(bean);
    }
    @Override public void removeBean(T bean) {
        getBeans().remove(bean);
    }
    @Override public void removeAllBeans() {
        getBeans().clear();
    }
}
