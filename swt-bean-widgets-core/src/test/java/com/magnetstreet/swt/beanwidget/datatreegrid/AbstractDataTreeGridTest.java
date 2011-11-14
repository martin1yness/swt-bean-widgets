package com.magnetstreet.swt.beanwidget.datatreegrid;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willCallRealMethod;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.*;

/**
 * AbstractDataTreeGridTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2011-11-10
 */
@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AbstractDataTreeGridTest {
    public static class TestBean implements Comparable<TestBean> {
        private String objectOne = "One";
        private TestBean2 testBean2 = new TestBean2();
        private List<TestBean2> testBean2s = new ArrayList<TestBean2>();
        public TestBean() {
            testBean2s.add(new TestBean2());
            testBean2s.add(new TestBean2());
            testBean2s.add(new TestBean2());
        }
        @Override public int compareTo(TestBean o) {
            return objectOne.compareTo(o.objectOne);
        }

        public String getObjectOne() {
            return objectOne;
        }

        public void setObjectOne(String objectOne) {
            this.objectOne = objectOne;
        }

        public TestBean2 getTestBean2() {
            return testBean2;
        }

        public void setTestBean2(TestBean2 testBean2) {
            this.testBean2 = testBean2;
        }

        public List<TestBean2> getTestBean2s() {
            return testBean2s;
        }

        public void setTestBean2s(List<TestBean2> testBean2s) {
            this.testBean2s = testBean2s;
        }
    }
    public static class TestBean2 implements Comparable<TestBean2> {
        private Integer id = Math.round(Math.round(Math.random() * Integer.MAX_VALUE));

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override public int compareTo(TestBean2 o) {
            return id.compareTo(o.id);
        }
    }

    @Mock AbstractDataTreeGrid<TestBean> dataTreeGrid;
    @Mock CheckboxTreeViewer treeViewer;
    @Mock Tree tree;
    @Mock TreeItem treeItemA, treeItemB, treeItemC, treeItemA_A, treeItemA_B, treeItemA_C,
            treeItemB_A, treeItemB_B, treeItemB_C, treeItemC_A, treeItemC_B, treeItemC_C;
    TestBean beanA, beanB, beanC;
    ArrayList<TestBean> testBeans = new ArrayList<TestBean>(3);

    @Before
    public void createImaginaryTreeGrid() {
        testBeans.add(beanA = new TestBean());
        testBeans.add(beanB = new TestBean());
        testBeans.add(beanC = new TestBean());

        given(dataTreeGrid.getTreeViewer()).willReturn(treeViewer);
        given(dataTreeGrid.getBeans()).willReturn(testBeans);
        given(treeViewer.getTree()).willReturn(tree);
        given(tree.getStyle()).willReturn(SWT.CHECK|SWT.MULTI);
        given(tree.getItemCount()).willReturn(3);
        given(tree.getItems()).willReturn(new TreeItem[]{treeItemA, treeItemB, treeItemC});

        given(treeItemA_A.getData()).willReturn(new TreeNode(beanA.getTestBean2s().get(0)));
        given(treeItemA_A.getItemCount()).willReturn(0);
        given(treeItemA_B.getData()).willReturn(new TreeNode(beanA.getTestBean2s().get(1)));
        given(treeItemA_B.getItemCount()).willReturn(0);
        given(treeItemA_C.getData()).willReturn(new TreeNode(beanA.getTestBean2s().get(2)));
        given(treeItemA_C.getItemCount()).willReturn(0);
        given(treeItemB_A.getData()).willReturn(new TreeNode(beanB.getTestBean2s().get(0)));
        given(treeItemB_A.getItemCount()).willReturn(0);
        given(treeItemB_B.getData()).willReturn(new TreeNode(beanB.getTestBean2s().get(1)));
        given(treeItemB_B.getItemCount()).willReturn(0);
        given(treeItemB_C.getData()).willReturn(new TreeNode(beanB.getTestBean2s().get(2)));
        given(treeItemB_C.getItemCount()).willReturn(0);
        given(treeItemC_A.getData()).willReturn(new TreeNode(beanC.getTestBean2s().get(0)));
        given(treeItemC_A.getItemCount()).willReturn(0);
        given(treeItemC_B.getData()).willReturn(new TreeNode(beanC.getTestBean2s().get(1)));
        given(treeItemC_B.getItemCount()).willReturn(0);
        given(treeItemC_C.getData()).willReturn(new TreeNode(beanC.getTestBean2s().get(2)));
        given(treeItemC_C.getItemCount()).willReturn(0);

        given(treeItemA.getData()).willReturn(new TreeNode(beanA));
        given(treeItemA.getItemCount()).willReturn(beanA.getTestBean2s().size());
        given(treeItemA.getItems()).willReturn(new TreeItem[]{treeItemA_A, treeItemA_B, treeItemA_C});
        given(treeItemA.getExpanded()).willReturn(false);

        given(treeItemB.getData()).willReturn(new TreeNode(beanB));
        given(treeItemB.getItemCount()).willReturn(beanB.getTestBean2s().size());
        given(treeItemB.getItems()).willReturn(new TreeItem[]{treeItemB_A, treeItemB_B, treeItemB_C});
        given(treeItemB.getExpanded()).willReturn(false);

        given(treeItemC.getData()).willReturn(new TreeNode(beanC));
        given(treeItemC.getItemCount()).willReturn(beanC.getTestBean2s().size());
        given(treeItemC.getItems()).willReturn(new TreeItem[]{treeItemC_A, treeItemC_B, treeItemC_C});
        given(treeItemC.getExpanded()).willReturn(false);
    }

    @Test public void testRecursiveGetTreeItems_visibleOnlyRootElements_allGivenRootElementsReturned() {
        given(dataTreeGrid.recursiveGetTreeItems(eq(true), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();
        List<TreeItem> items = dataTreeGrid.recursiveGetTreeItems(true, new TreeItem[]{treeItemA, treeItemB, treeItemC}, beanA, beanC);

        assertThat(items, hasItems(treeItemA, treeItemC));
    }

    @Test public void testRecursiveGetTreeItems_visibleOnlyElementsRootAndChildren_allVisibleReturned() {
        given(dataTreeGrid.recursiveGetTreeItems(eq(true), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();
        List<TreeItem> items = dataTreeGrid.recursiveGetTreeItems(true, new TreeItem[]{treeItemA, treeItemB, treeItemC}, beanA, beanC.getTestBean2s().get(0), beanC.getTestBean2s().get(2));

        assertThat(items, hasItems(treeItemA));
    }

    @Test public void testRecursiveGetTreeItems_visibleOnlyElementsRootAndChildrenExpanded_allVisibleReturned() {
        given(dataTreeGrid.recursiveGetTreeItems(eq(true), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();
        given(treeItemC.getExpanded()).willReturn(true);
        List<TreeItem> items = dataTreeGrid.recursiveGetTreeItems(true, new TreeItem[]{treeItemA, treeItemB, treeItemC}, beanA, beanC.getTestBean2s().get(0), beanC.getTestBean2s().get(2));

        assertThat(items, hasItems(treeItemA, treeItemC_A, treeItemC_C));
    }

    @Test public void testRecursiveGetTreeItems_elementsRootAndChildren_allVisibleReturned() {
        given(dataTreeGrid.recursiveGetTreeItems(eq(false), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();
        given(treeItemC.getExpanded()).willReturn(false);
        List<TreeItem> items = dataTreeGrid.recursiveGetTreeItems(false, new TreeItem[]{treeItemA, treeItemB, treeItemC}, beanA, beanC.getTestBean2s().get(0), beanC.getTestBean2s().get(2));

        assertThat(items, hasItems(treeItemA, treeItemC_A, treeItemC_C));
    }

    @Test public void testCheckAll_checkAllItemsVisible() {
        willCallRealMethod().given(dataTreeGrid).checkAllItems();
        ArrayList list = new ArrayList();
        list.add(treeItemA);
        list.add(treeItemA_A);
        list.add(treeItemA_B);
        list.add(treeItemA_C);
        list.add(treeItemB);
        list.add(treeItemB_A);
        list.add(treeItemB_B);
        list.add(treeItemB_C);
        list.add(treeItemC);
        list.add(treeItemC_A);
        list.add(treeItemC_B);
        list.add(treeItemC_C);
        given(dataTreeGrid.recursiveGetTreeItems(eq(true), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willReturn(list);

        dataTreeGrid.checkAllItems();

        verify(treeItemA, new Times(1)).setChecked(eq(true));
        verify(treeItemA_A, new Times(1)).setChecked(eq(true));
        verify(treeItemA_B, new Times(1)).setChecked(eq(true));
        verify(treeItemA_C, new Times(1)).setChecked(eq(true));
        verify(treeItemB, new Times(1)).setChecked(eq(true));
        verify(treeItemB_A, new Times(1)).setChecked(eq(true));
        verify(treeItemB_B, new Times(1)).setChecked(eq(true));
        verify(treeItemB_C, new Times(1)).setChecked(eq(true));
        verify(treeItemC, new Times(1)).setChecked(eq(true));
        verify(treeItemC_A, new Times(1)).setChecked(eq(true));
        verify(treeItemC_B, new Times(1)).setChecked(eq(true));
        verify(treeItemC_C, new Times(1)).setChecked(eq(true));
    }

    @Test public void testCheckBeans_givenEmptySet_checkNothing() {
        willCallRealMethod().given(dataTreeGrid).checkBeans(anyCollection());
        dataTreeGrid.checkBeans(new ArrayList());
        verify(dataTreeGrid, new Times(0)).recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg());
        verifyZeroInteractions(treeItemA, treeItemB, treeItemC, treeItemA_A, treeItemA_B, treeItemA_C, treeItemB_A,
                treeItemB_B, treeItemB_C, treeItemC_A, treeItemC_B, treeItemC_C);
    }

    @Test public void testCheckBeans_givenNull_checkNothing() {
        willCallRealMethod().given(dataTreeGrid).checkBeans(null);
        dataTreeGrid.checkBeans(null);
        verify(dataTreeGrid, new Times(0)).recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg());
        verifyZeroInteractions(treeItemA, treeItemB, treeItemC, treeItemA_A, treeItemA_B, treeItemA_C, treeItemB_A,
                treeItemB_B, treeItemB_C, treeItemC_A, treeItemC_B, treeItemC_C);
    }

    @Test public void testCheckBeans_givenRootOnlyBeans_checkRootTreeItemsNotExpandedChildren() {
        given(treeItemC.getExpanded()).willReturn(true);
        willCallRealMethod().given(dataTreeGrid).checkBeans(anyCollection());
        given(dataTreeGrid.recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();

        List beans = new ArrayList();
        beans.add(beanA);
        beans.add(beanC);
        dataTreeGrid.checkBeans(beans);

        verify(treeItemA).setChecked(true);
        verify(treeItemC).setChecked(true);
        verify(treeItemB, new Times(0)).setChecked(anyBoolean());
        verify(treeItemB_A, new Times(0)).setChecked(anyBoolean());
        verify(treeItemB_B, new Times(0)).setChecked(anyBoolean());
        verify(treeItemB_C, new Times(0)).setChecked(anyBoolean());
        verify(treeItemA_A, new Times(0)).setChecked(anyBoolean());
        verify(treeItemA_B, new Times(0)).setChecked(anyBoolean());
        verify(treeItemA_C, new Times(0)).setChecked(anyBoolean());
        verify(treeItemC_A, new Times(0)).setChecked(anyBoolean());
        verify(treeItemC_B, new Times(0)).setChecked(anyBoolean());
        verify(treeItemC_C, new Times(0)).setChecked(anyBoolean());
    }

    @Test public void testCheckBeans_givenRootAndChildrenBeans_checkOnlyGivenChildrenAndParents() {
        given(treeItemB.getExpanded()).willReturn(true);
        given(treeItemC.getExpanded()).willReturn(true);
        willCallRealMethod().given(dataTreeGrid).checkBeans(anyCollection());
        given(dataTreeGrid.recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();

        List beans = new ArrayList();
        beans.add(beanA);
        beans.add(beanC);
        beans.add(beanC.getTestBean2s().get(0));
        beans.add(beanB.getTestBean2s().get(2));
        dataTreeGrid.checkBeans(beans);

        verify(treeItemA).setChecked(true);
        verify(treeItemC).setChecked(true);
        verify(treeItemC_A).setChecked(true);
        verify(treeItemB_C).setChecked(true);
        verify(treeItemA_A, new Times(0)).setChecked(anyBoolean());
        verify(treeItemA_B, new Times(0)).setChecked(anyBoolean());
        verify(treeItemA_C, new Times(0)).setChecked(anyBoolean());
        verify(treeItemB, new Times(0)).setChecked(anyBoolean());
        verify(treeItemB_A, new Times(0)).setChecked(anyBoolean());
        verify(treeItemB_B, new Times(0)).setChecked(anyBoolean());
        verify(treeItemC_B, new Times(0)).setChecked(anyBoolean());
        verify(treeItemC_C, new Times(0)).setChecked(anyBoolean());
    }

    @Test public void testSelectAll_selectionCreatedWithAllVisible() {
        willCallRealMethod().given(dataTreeGrid).selectAllItems();
        given(treeItemB.getExpanded()).willReturn(true);
        List<TreeItem> expandedItems = new ArrayList<TreeItem>();
        expandedItems.add(treeItemA);
        expandedItems.add(treeItemB);
        expandedItems.add(treeItemB_A);
        expandedItems.add(treeItemB_B);
        expandedItems.add(treeItemB_C);
        expandedItems.add(treeItemC);
        given(dataTreeGrid.recursiveGetTreeItems(eq(true), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willReturn(expandedItems);

        dataTreeGrid.selectAllItems();
        verify(treeViewer).setSelection(argThat(new ArgumentMatcher<StructuredSelection>() {
            @Override public boolean matches(Object argument) {
                assertThat(((StructuredSelection)argument).toList(), hasItems(treeItemA.getData(),treeItemB.getData(),treeItemB_A.getData(),treeItemB_B.getData(),treeItemB_C.getData(), treeItemC.getData()));
                return true;
            }
        }));
    }

    @Test public void testSelectBeans_givenEmptySet_selectNone() {
        willCallRealMethod().given(dataTreeGrid).selectBeans(anyCollection());
        dataTreeGrid.selectBeans(new ArrayList());
        verify(dataTreeGrid, new Times(0)).recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg());
        verifyZeroInteractions(treeItemA, treeItemB, treeItemC, treeItemA_A, treeItemA_B, treeItemA_C, treeItemB_A,
                treeItemB_B, treeItemB_C, treeItemC_A, treeItemC_B, treeItemC_C);
    }

    @Test public void testSelectBeans_givenNull_selectNone() {
        willCallRealMethod().given(dataTreeGrid).selectBeans(null);
        dataTreeGrid.selectBeans(null);
        verify(dataTreeGrid, new Times(0)).recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg());
        verifyZeroInteractions(treeItemA, treeItemB, treeItemC, treeItemA_A, treeItemA_B, treeItemA_C, treeItemB_A,
                treeItemB_B, treeItemB_C, treeItemC_A, treeItemC_B, treeItemC_C);
    }

    @Test public void testSelectBeans_givenRootOnlyBeans_selectRootTreeItemsNotExpandedChildren() {
        willCallRealMethod().given(dataTreeGrid).selectBeans(anyCollection());
        given(dataTreeGrid.recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();
        given(dataTreeGrid.getSelectedBeans()).willReturn(new ArrayList());

        ArrayList beans = new ArrayList();
        beans.add(beanA);
        beans.add(beanB);
        dataTreeGrid.selectBeans(beans);

        verify(treeViewer).setSelection(argThat(new ArgumentMatcher<StructuredSelection>() {
            @Override
            public boolean matches(Object argument) {
                assertThat(((StructuredSelection) argument).toList(), hasItems(treeItemA.getData(), treeItemB.getData()));
                return true;
            }
        }));
    }

    @Test public void testSelectBeans_givenRootAndChildrenBeans_selectOnlyGivenParentsAndChildren() {
        willCallRealMethod().given(dataTreeGrid).selectBeans(anyCollection());
        given(treeItemC.getExpanded()).willReturn(true);
        given(dataTreeGrid.recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();
        given(dataTreeGrid.getSelectedBeans()).willReturn(new ArrayList());

        ArrayList beans = new ArrayList();
        beans.add(beanA);
        beans.add(beanB);
        beans.add(beanC.getTestBean2s().get(1));
        dataTreeGrid.selectBeans(beans);

        verify(treeViewer).setSelection(argThat(new ArgumentMatcher<StructuredSelection>() {
            @Override public boolean matches(Object argument) {
                assertThat(((StructuredSelection)argument).toList(), hasItems(treeItemA.getData(), treeItemB.getData(), treeItemC_B.getData()));
                assertThat(((StructuredSelection)argument).toList(), not(hasItems(treeItemC.getData())));
                return true;
            }
        }));
    }

    @Test public void testSelectBeans_givenPreviousSelection_selectInAdditionTo() {
        willCallRealMethod().given(dataTreeGrid).selectBeans(anyCollection());
        given(treeItemC.getExpanded()).willReturn(true);
        given(dataTreeGrid.recursiveGetTreeItems(anyBoolean(), Matchers.<TreeItem[]>anyVararg(), anyVararg())).willCallRealMethod();
        ArrayList selectedBeans = new ArrayList();
        selectedBeans.add(beanA);
        given(dataTreeGrid.getSelectedBeans()).willReturn(selectedBeans);

        ArrayList beans = new ArrayList();
        beans.add(beanB);
        beans.add(beanC.getTestBean2s().get(1));
        dataTreeGrid.selectBeans(beans);

        verify(treeViewer).setSelection(argThat(new ArgumentMatcher<StructuredSelection>() {
            @Override public boolean matches(Object argument) {
                assertThat(((StructuredSelection)argument).toList(), hasItems(treeItemA.getData(), treeItemB.getData(), treeItemC_B.getData()));
                assertThat(((StructuredSelection)argument).toList(), not(hasItems(treeItemC.getData())));
                return true;
            }
        }));
    }
}
