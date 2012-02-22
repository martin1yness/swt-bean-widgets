package com.magnetstreet.swt.beanwidget.datagrid2;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * GroupedDataTreeGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2012-02-21
 */
public abstract class GroupedDataTreeGrid<T extends Comparable<T>> extends ReflectiveDataTreeGrid<T> {

    /**
     * Defines a grouping of beans per implementation of the #matches(T bean). Priority property defines the
     * order of the groups in the display.
     * @param <T>
     */
    public abstract static class Group<T> {
        protected final String groupName;
        protected final int priority;

        public Group(String groupName, int priority) {
            this.groupName = groupName;
            this.priority = priority;
        }

        public abstract boolean matches(T bean);
    }
    
    private List<Group<T>> groups = new ArrayList<Group<T>>();
    
    private Map<Group<T>, List<T>> groupToBeansMap = new HashMap<Group<T>, List<T>>();
    private Map<T, Group<T>> beanToGroupMap = new HashMap<T, Group<T>>();
    
    public GroupedDataTreeGrid(Composite composite, int i) {
        super(composite, i);
    }

    @Override public void refresh() {
        super.refresh();
        applyGroupingsToBeans();
    }

    @Override protected int compareTreeNodes(TreeNode nodeA, TreeNode nodeB, String identifier, boolean direction) {
        // Check if in different group
        int grpCmp = compareTreeNodesByGroup(nodeA, nodeB);
        switch (grpCmp) {
            case 0: // In same group apply standard sorting logic
                return compareTreeNodesWithSortingDefinitionsByColumnIdentifier(nodeA, nodeB, identifier, direction);
            default:
                return grpCmp;
        }
    }

    protected List<Group<T>> getGroups() {
        return groups;
    }
    protected Map<Group<T>, List<T>> getGroupToBeansMap() {
        return groupToBeansMap;
    }
    protected Map<T, Group<T>> getBeanToGroupMap() {
        return beanToGroupMap;
    }

    protected int compareTreeNodesByGroup(TreeNode nodeA, TreeNode nodeB) {
        Object beanA = ((TreeNode)nodeA).getValue();
        Object beanB = ((TreeNode)nodeB).getValue();

        if(getBeanToGroupMap().containsKey(beanA) && getBeanToGroupMap().containsKey(beanB)) {
            return getBeanToGroupMap().get(beanA).priority - getBeanToGroupMap().get(beanB).priority;
        } else if(getBeanToGroupMap().containsKey(beanA))
            return 1;
        else if(getBeanToGroupMap().containsKey(beanB))
            return -1;
        return 0;
    }

    protected void applyGroupingsToBeans() {
        getGroupToBeansMap().clear();
        getBeanToGroupMap().clear();

        Collections.sort(getGroups(), new Comparator<Group<T>>() {
            @Override public int compare(Group<T> o1, Group<T> o2) {
                return o1.priority - o2.priority;
            }
        });

        for(Group<T> group : getGroups()) {
            for(T bean: getBeans()) {
                if(!getGroupToBeansMap().containsKey(group))
                    getGroupToBeansMap().put(group, new LinkedList<T>());
                
                if(group.matches(bean)) {
                    getGroupToBeansMap().get(group).add(bean);
                    getBeanToGroupMap().put(bean, group);
                }
            }
        }
    }
    
    public void addGroup(Group<T> beanGroupPredicate) {
        if(groups==null)
            groups = new ArrayList<Group<T>>();
        getGroups().add(beanGroupPredicate);
    }
    
    public void clearGroups() {
        getGroups().clear();
        getGroupToBeansMap().clear();
        getBeanToGroupMap().clear();
    }
}
