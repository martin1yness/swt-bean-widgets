package com.magnetstreet.swt.beanwidget.datagrid2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.eclipse.jface.viewers.TreeNode;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willCallRealMethod;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;

/**
 * GroupedDataTreeGridTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2012-02-21
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupedDataTreeGridTest {
    public static class TestBean implements Comparable<TestBean> {
        protected Integer id;
        public TestBean(Integer id) {
            this.id = id;
        }
        @Override public int compareTo(TestBean o) {
            return id.compareTo(o.id);
        }
    }
    
    @Mock GroupedDataTreeGrid<GroupedDataTreeGridTest.TestBean> groupedDataTreeGrid;
    
    private TestBean beanA = new TestBean(1), beanB = new TestBean(2);
    private TreeNode nodeA = new TreeNode(beanA), nodeB = new TreeNode(beanB);
    private List<GroupedDataTreeGrid.Group<TestBean>> groups = new ArrayList<GroupedDataTreeGrid.Group<TestBean>>();
    private Map<GroupedDataTreeGrid.Group<TestBean>, List<TestBean>> groupToBeanMap = new HashMap<GroupedDataTreeGrid.Group<TestBean>, List<TestBean>>();
    private Map<TestBean, GroupedDataTreeGrid.Group<TestBean>> beanToGroupMap = new HashMap<TestBean, GroupedDataTreeGrid.Group<TestBean>>();
    private GroupedDataTreeGrid.Group<TestBean> groupA, groupB, groupA_B;
    
    @Before public void setUp() {
        groupA = new GroupedDataTreeGrid.Group<TestBean>("groupA", 10) {
            @Override public boolean matches(TestBean bean) {
                if(bean == beanA)
                    return true;
                return false;
            }
        };
        groupB = new GroupedDataTreeGrid.Group<TestBean>("groupB", 5) {
            @Override public boolean matches(TestBean bean) {
                if(bean == beanB)
                    return true;
                return false;
            }
        };
        groupA_B = new GroupedDataTreeGrid.Group<TestBean>("groupA_B", 1) {
            @Override public boolean matches(TestBean bean) {
                if(bean == beanA || bean == beanB)
                    return true;
                return false;
            }
        };
        
        groups.add(groupA);
        groups.add(groupB);

        groupToBeanMap.put(groupA, Lists.newArrayList(beanA));
        groupToBeanMap.put(groupB, Lists.newArrayList(beanB));
        
        beanToGroupMap.put(beanA, groupA);
        beanToGroupMap.put(beanB, groupB);

        given(groupedDataTreeGrid.getGroups()).willReturn(groups);
        given(groupedDataTreeGrid.getBeanToGroupMap()).willReturn(beanToGroupMap);
        given(groupedDataTreeGrid.getGroupToBeansMap()).willReturn(groupToBeanMap);
    }
    
    @Test public void testGroupCompare_noGroups_beansAreEqual() {
        given(groupedDataTreeGrid.compareTreeNodesByGroup((TreeNode)any(), (TreeNode)any())).willCallRealMethod();

        given(groupedDataTreeGrid.getGroups()).willReturn(new ArrayList<GroupedDataTreeGrid.Group<TestBean>>());
        given(groupedDataTreeGrid.getBeanToGroupMap()).willReturn(new HashMap<TestBean, GroupedDataTreeGrid.Group<TestBean>>());
        given(groupedDataTreeGrid.getGroupToBeansMap()).willReturn(new HashMap<GroupedDataTreeGrid.Group<TestBean>, List<TestBean>>());

        int cmp = groupedDataTreeGrid.compareTreeNodesByGroup(nodeA, nodeB);
        assertThat(cmp, Matchers.is(0));
    }

    @Test public void testGroupCompare_sameGroup_beansAreEqual() {
        given(groupedDataTreeGrid.compareTreeNodesByGroup((TreeNode)any(), (TreeNode)any())).willCallRealMethod();

        groups.clear();
        groups.add(groupA_B);

        groupToBeanMap.clear();
        groupToBeanMap.put(groupA, Lists.newArrayList(beanA,beanB));

        beanToGroupMap.clear();
        beanToGroupMap.put(beanA, groupA_B);
        beanToGroupMap.put(beanB, groupA_B);

        int cmp = groupedDataTreeGrid.compareTreeNodesByGroup(nodeA, nodeB);
        assertThat(cmp, Matchers.is(0));
    }

    @Test public void testGroupCompare_AinGroup_beanAGreater() {
        given(groupedDataTreeGrid.compareTreeNodesByGroup((TreeNode)any(), (TreeNode)any())).willCallRealMethod();

        groups.clear();
        groups.add(groupA);

        groupToBeanMap.clear();
        groupToBeanMap.put(groupA, Lists.newArrayList(beanA));

        beanToGroupMap.clear();
        beanToGroupMap.put(beanA, groupA_B);

        int cmp = groupedDataTreeGrid.compareTreeNodesByGroup(nodeA, nodeB);
        assertThat(cmp, Matchers.greaterThan(0));
    }

    @Test public void testGroupCompare_AinGroup_beanBLessThan() {
        given(groupedDataTreeGrid.compareTreeNodesByGroup((TreeNode)any(), (TreeNode)any())).willCallRealMethod();

        groups.clear();
        groups.add(groupA);

        groupToBeanMap.clear();
        groupToBeanMap.put(groupA, Lists.newArrayList(beanA));

        beanToGroupMap.clear();
        beanToGroupMap.put(beanA, groupA_B);

        int cmp = groupedDataTreeGrid.compareTreeNodesByGroup(nodeB, nodeA);
        assertThat(cmp, Matchers.lessThan(0));
    }

    @Test public void testGroupCompare_AinGroupABinGroupB_beanAGreater() {
        given(groupedDataTreeGrid.compareTreeNodesByGroup((TreeNode)any(), (TreeNode)any())).willCallRealMethod();
        
        int cmp = groupedDataTreeGrid.compareTreeNodesByGroup(nodeA, nodeB);
        assertThat(cmp, Matchers.greaterThan(0));
    }

    @Test public void testCompareTreeNodesWithSortingDefinitionsByColumnIdentifier_diffGroups_returnGroupCmp() {
        given(groupedDataTreeGrid.compareTreeNodes(eq(nodeA), eq(nodeB), anyString(), anyBoolean())).willCallRealMethod();
        given(groupedDataTreeGrid.compareTreeNodesByGroup((TreeNode) any(), (TreeNode) any())).willReturn(-1234);

        int cmp = groupedDataTreeGrid.compareTreeNodes(nodeA, nodeB, "colName", true);
        assertThat(cmp, Matchers.is(-1234));
    }
    
    @Test public void testCompareTreeNodesWithSortingDefinitionsByColumnIdentifier_sameGroups_callBaseClassSortingDefComparator() {
        given(groupedDataTreeGrid.compareTreeNodes(eq(nodeA), eq(nodeB), anyString(), anyBoolean())).willCallRealMethod();
        given(groupedDataTreeGrid.compareTreeNodesByGroup((TreeNode)any(), (TreeNode)any())).willReturn(0);
        
        int cmp = groupedDataTreeGrid.compareTreeNodes(nodeA, nodeB, "colName", true);
        verify(groupedDataTreeGrid).compareTreeNodesWithSortingDefinitionsByColumnIdentifier(eq(nodeA), eq(nodeB), eq("colName"), eq(true));
    }
    
    @Test public void testApplyGroupingsToBeans_noGroups_emptyMaps() {
        willCallRealMethod().given(groupedDataTreeGrid).applyGroupingsToBeans();
        given(groupedDataTreeGrid.getGroups()).willReturn(new ArrayList<GroupedDataTreeGrid.Group<TestBean>>(0));
        
        assertThat(groupToBeanMap.isEmpty(), Matchers.is(true));
        assertThat(beanToGroupMap.isEmpty(), Matchers.is(true));
        assertThat(groups.isEmpty(), Matchers.is(true));
    }
    
    @Test public void testApplyGroupingsToBeans_oneGroup_mapBeanAtoGroup() {
        willCallRealMethod().given(groupedDataTreeGrid).applyGroupingsToBeans();
        
        groups.clear();
        groups.add(groupA);
        
        given(groupedDataTreeGrid.getBeans()).willReturn(Lists.newArrayList(beanA,beanB));

        groupedDataTreeGrid.applyGroupingsToBeans();
        
        assertThat(groupToBeanMap.get(groupA).size(), Matchers.is(1));
        assertThat(groupToBeanMap.get(groupA).get(0), Matchers.is(beanA));
        assertThat(beanToGroupMap.size(), Matchers.is(1));
        assertThat(beanToGroupMap.get(beanA), Matchers.is(groupA));
    }

    @Test public void testApplyGroupingsToBeans_oneGroupForEachBean_mapBeanAtoGroupAandBtoB() {
        willCallRealMethod().given(groupedDataTreeGrid).applyGroupingsToBeans();

        given(groupedDataTreeGrid.getBeans()).willReturn(Lists.newArrayList(beanA,beanB));

        groupedDataTreeGrid.applyGroupingsToBeans();

        assertThat(groupToBeanMap.get(groupA).size(), Matchers.is(1));
        assertThat(groupToBeanMap.get(groupA).get(0), Matchers.is(beanA));
        assertThat(groupToBeanMap.get(groupB).size(), Matchers.is(1));
        assertThat(groupToBeanMap.get(groupB).get(0), Matchers.is(beanB));
        assertThat(beanToGroupMap.size(), Matchers.is(2));
        assertThat(beanToGroupMap.get(beanA), Matchers.is(groupA));
        assertThat(beanToGroupMap.get(beanB), Matchers.is(groupB));
    }

    @Test public void testApplyGroupingsToBeans_oneGroupForBothBean_mapBeanAandBtoGroupA_B() {
        willCallRealMethod().given(groupedDataTreeGrid).applyGroupingsToBeans();
        
        groups.clear();
        groups.add(groupA_B);

        given(groupedDataTreeGrid.getBeans()).willReturn(Lists.newArrayList(beanA,beanB));

        groupedDataTreeGrid.applyGroupingsToBeans();

        assertThat(groupToBeanMap.get(groupA_B).size(), Matchers.is(2));
        assertThat(groupToBeanMap.get(groupA_B), JUnitMatchers.hasItems(beanA, beanB));
        assertThat(beanToGroupMap.size(), Matchers.is(2));
        assertThat(beanToGroupMap.get(beanA), Matchers.is(groupA_B));
        assertThat(beanToGroupMap.get(beanB), Matchers.is(groupA_B));
    }
}
