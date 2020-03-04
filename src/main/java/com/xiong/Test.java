package com.xiong;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.sun.tools.javac.comp.Enter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.avro.generic.GenericData;
import org.apache.commons.collections.list.AbstractLinkedList;
import org.apache.hadoop.util.hash.Hash;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.util.IntegerField;
import org.apache.poi.util.SystemOutLogger;
import redis.clients.jedis.Jedis;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.reflect.generics.tree.Tree;
import sun.text.normalizer.Trie;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Test {
    private boolean flag = false;
    private char c;

    public static void test() throws MalformedURLException {
        try {
            URL url = new URL("https://mil.ifeng.com/");
            InputStream in = url.openStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader bufr = new BufferedReader(isr);
            String str;
            while ((str = bufr.readLine()) != null) {
                System.out.println(str);
            }
            bufr.close();
            isr.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void redis() {
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping() + jedis.isConnected());
        String[] str = new String[]{"POSITION_TAG01", "POSITION_TAG02", "POSITION_TAG03", "POSITION_TAG04", "POSITION_TAG05"};
        for (int i = 0; i < str.length; i++)
            for (int j = 0; j < 500; j++)
                jedis.lpush(str[i], j + "");
        for (int i = 0; i < str.length; i++) {
            if (jedis.llen(str[i]) > 100) {
                List<String> list = jedis.lrange(str[i], 101, jedis.llen(str[i]));
                System.out.println(list);
                jedis.ltrim(str[i], 0, 100);
            }
        }
    }

    public static boolean isValidSerialization(String preorder) {
        int res = 1;
        String[] str = preorder.split(",");
        for (int i = 0; i < str.length; i++) {
            if (res <= 0)
                return false;
            if (str[i].equals("#")) {
                res -= 1;
            } else
                res += 1;
        }
        return res <= 0;
    }


    public static int minIncrementForUnique(int[] A) {
        Arrays.sort(A);
        int res = 0;
        for (int i = 1; i < A.length; i++)
            if (A[i] <= A[i - 1]) {
                res += A[i - 1] - A[i] + 1;
                A[i] = A[i - 1] + 1;
            }
        return res;
    }

    public static void rotateString(char[] str, int offset) {
        if (str == null || str.length == 0)
            return;
        int len = str.length;
        offset = offset % len;
        reverse(str, 0, len - offset - 1);
        reverse(str, len - offset, len - 1);
        reverse(str, 0, len - 1);
    }

    public static void reverse(char[] str, int start, int end) {
        while (start <= end) {
            char temp = str[start];
            str[start] = str[end];
            str[end] = temp;
            start++;
            end--;
        }

    }


    public void sort(int[] a, int low, int high) {
        int start = low;
        int end = high;
        int key = a[low];
        while (end > start) {
            //从后往前比较
            while (end > start && a[end] >= key)  //如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
                end--;
            if (a[end] <= key) {
                int temp = a[end];
                a[end] = a[start];
                a[start] = temp;
            }
            //从前往后比较
            while (end > start && a[start] <= key)//如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
                start++;
            if (a[start] >= key) {
                int temp = a[start];
                a[start] = a[end];
                a[end] = temp;
            }
            //此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        //递归
        if (start > low) sort(a, low, start - 1);//左边序列。第一个索引位置到关键值索引-1
        if (end < high) sort(a, end + 1, high);//右边序列。从关键值索引+1到最后一个
    }


    public static String reverseWords1(String s) {
        StringBuffer sb = new StringBuffer(s.trim());
        StringBuffer result = new StringBuffer();
        String[] str = sb.toString().split("\\s+");
        System.out.println(Arrays.toString(str));
        for (int i = str.length - 1; i >= 1; i--) {
            result.append(str[i] + " ");
        }
        return result.append(str[0]).toString();
    }

    public static int mostFrequentlyAppearingLetters(String str) {
        if (str == null || str.length() == 0)
            return 0;
        Map<Character, Integer> map = new HashMap<>();
        int max = 0;
        for (int i = 0; i < str.length(); i++) {
            if (map.containsKey(str.charAt(i)))
                map.put(str.charAt(i), map.get(str.charAt(i)) + 1);
            else
                map.put(str.charAt(i), 1);
        }
        for (Integer i : map.values()) {
            max = Math.max(max, i);
        }
        return max;
    }

    public static int deduplication(int[] nums) {
        Arrays.sort(nums);

        int i = 0, j = 1;
        while (j < nums.length) {
            if (nums[j] != nums[i]) {
                swap(nums, ++i, j);
            }
            j++;
        }

        return i + 1;
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[j];
        nums[j] = nums[i];
        nums[i] = temp;
    }

    public static void sortIntegers(int[] A) {
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A.length - i - 1; j++)
                if (A[j] > A[j + 1]) {
                    int tmp = A[j];
                    A[j] = A[j + 1];
                    A[j + 1] = tmp;
                }
    }

    /*
    /insert into node
     */
    public ListNode insertNode(ListNode head, int val) {
        if (head == null)
            return null;
        ListNode prev = head, curr = head.next;
        if (prev != null && head.val >= val) {
            ListNode res = new ListNode(val);
            res.next = head;
            return res;
        }
        while (curr != null) {
            if (curr.val > val) {
                ListNode insert_node = new ListNode(val);
                prev.next = insert_node;
                insert_node.next = curr;
                return head;
            }
            prev = prev.next;
            curr = curr.next;
        }
        prev.next = new ListNode(val);
        return head;
    }

    public int countNodesII(ListNode head) {
        int count = 0;
        while (head != null) {
            if (head.val >= 0 && head.val % 2 == 1)
                count++;
            head = head.next;
        }
        return count;
    }

    public boolean findHer(String[] maze) {
        // Write your code here
        boolean[][] S = new boolean[maze.length][maze[0].length()];
        boolean[][] T = new boolean[maze.length][maze[0].length()];
        int indexSx = 0, indexSy = 0, indexTx = 0, indexTy = 0;
        for (int i = 0; i < maze.length; i++)
            for (int j = 0; j < maze[0].length(); j++) {
                if (maze[i].charAt(j) == 'S') {
                    indexSx = i;
                    indexSy = j;
                } else if (maze[i].charAt(j) == 'T') {
                    indexTx = i;
                    indexTy = j;
                }
            }
        S[indexSx][indexSy] = true;
        T[indexTx][indexTy] = true;
        dfs(maze, indexSx, indexSy, S);
        dfs(maze, indexTx, indexTy, T);
        for (int i = 0; i < maze.length; i++)
            for (int j = 0; j < maze[0].length(); j++)
                if (S[i][j] && T[i][j])
                    return true;
        return false;
    }

    public void dfs(String[] maze, int x, int y, boolean[][] flag) {
        if (x > 0 && y > 0) {
            if (!flag[x - 1][y - 1] && maze[x - 1].charAt(y - 1) == '.') {
                flag[x - 1][y - 1] = true;
                dfs(maze, x - 1, y - 1, flag);
            }
        }

    }


    public static class Interval {
        int start, end;

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static long intervalStatistics(int[] arr, int k) {
        long res = 0;
        int count;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1)
                continue;
            count = 0;
            for (int j = i; j < arr.length; j++) {
                if (arr[j] == 1) {
                    count++;
                    if (count > k)
                        break;
                } else
                    res++;
            }
        }
        return res;
    }

    public static int getAns(String s, int k) {
        if (k == 1)
            return s.length();
        int start = 0, end = 0, len = s.length(), res = 0;
        for (int i = 0; i < len - 1; i++) {
            if (s.charAt(i) != s.charAt(i + 1)) {
                res += Math.ceil((double) (end - start + 1) / k);
                start = i + 1;
            }
            end = i + 1;
        }
        return res;
    }

    public static int getAns(List<Interval> a) {
        int size = a.size();
        if (size == 0)
            return 0;
        Collections.sort(a, new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                if (o1.end == o2.end)
                    return o1.start - o2.start;
                return o1.end - o2.end;
            }
        });
        int end = a.get(0).end, sum = 1;
        for (int i = 1; i < size; i++) {
            if (a.get(i).start > end) {
                sum++;
                end = a.get(i).end;
            }
        }
        return sum;
    }

    public static long doingHomework(int[] cost, int[] val) {
        // Write your code here.
        int[] tmp = new int[cost.length + 1];
        int sum = 0;
        for (int i = 0; i < cost.length; i++) {
            sum += cost[i];
            tmp[i + 1] = sum;
        }
        int start, end;
        long res = 0;
        for (int i = 0; i < val.length; i++) {
            if (val[i] >= tmp[cost.length]) {
                res += tmp[cost.length];
                continue;
            }
            start = 0;
            end = cost.length - 1;
            while (start <= end) {
                int middle = (start + end) / 2;
                if (val[i] == tmp[middle]) {
                    res += val[i];
                    break;
                } else if (val[i] < tmp[middle]) {
                    if (middle != 0 && val[i] >= tmp[middle - 1]) {
                        res += tmp[middle - 1];
                        break;
                    } else
                        end = middle - 1;
                } else {
                    if (middle != cost.length + 1 && val[i] < tmp[middle + 1]) {
                        res += tmp[middle];
                        break;
                    } else
                        start = middle + 1;
                }
            }
        }
        return res;
    }

    public int getDistance(int n, int m, int target, List<Integer> d) {
        if (n == 0)
            return target;
        int l = 0, r = d.get(n - 1), ans;
        while (l <= r) {
            int middle = (l + r) / 2;
            if (check(d, middle, n, m))
                l = middle + 1;
            else
                r = middle - 1;
        }
        return l - 1;
    }

    private boolean check(List<Integer> list, int index, int n, int m) {
        int last = 0;
        int ans = 0;
        for (int i = 0; i <= n; i++) {
            if (list.get(i) - last < index)
                ans++;
            else
                last = list.get(i);
        }
        if (ans > m) return false;
        return true;
    }

    /*
    使用一个stack对原有stack进行排序
     */
    public void stackSorting(Stack<Integer> stk) {
        // write your code here
        if (stk.empty() | stk.size() == 1)
            return;
        Stack<Integer> tmpStack = new Stack();
        int first = stk.pop();
        tmpStack.push(first);
        stk.pop();
        //while()
    }

    public long playGames(int[] A) {
        // Write your code here
        int max = 0;
        for (int a : A) {
            max = Math.max(a, max);
        }
        long l = 0, r = max * 2;
        while (l < r) {
            long middle = (l + r) / 2;
            long cnt = 0;
            for (int a : A) {
                cnt += Math.max(middle - a, 0);
            }
            if (middle > cnt)
                l = middle + 1;
            else
                r = middle;
        }
        return Math.max(l, max);
    }

    public int leastInterval(char[] tasks, int n) {
        int[] vec = new int[26];
        for (char task : tasks)
            vec[task - 'A']++;
        Arrays.sort(vec);
        int i = 25, max = vec[25], len = tasks.length;
        while (i >= 0 && vec[i] == max)
            i--;
        return Math.max(len, (max - 1) * (n + 1) + 25 - i);
    }

    public int lengthOfLongestSubstring(String s) {
        int[] m = new int[256];
        int res = 0, left = 0;
        for (int i = 0; i < s.length(); i++) {
            if (m[s.charAt(i)] == 0 || m[s.charAt(i)] < left)
                res = Math.max(res, i - left + 1);
            else
                left = m[s.charAt(i)];
            m[s.charAt(i)] = i + 1;
        }
        return res;
    }

    /*
    判断pop是否可以由push构成
     */
    public boolean isLegalSeq(int pushStack[], int popStack[]) {
        // 放push元素的栈
        Stack stack = new Stack();
        // 当前入栈的标志位
        int pushTemp = 0;
        // 当前pop的标志位
        int popTemp = 0;
        //返回值标记
        boolean is = false;
        //如果序列长度不相等或为0则不符合条件，直接返回false
        if (pushStack.length != popStack.length || pushStack.length == 0) {
            return false;
        }
        // 检查当前pop标志位是否遍历完整个序列(注意用popTemp判断长度判断而不用pushTemp的长度判断)
        while (popTemp < popStack.length) {
            // 若栈为空或栈顶元素与当前pop标志位所在的元素值不同，则继续入栈
            while (stack.empty() || (Integer) stack.peek() != popStack[popTemp]) {
                //此时会出现不正确的pop情况
                if (pushTemp == pushStack.length) {
                    return false;
                }
                stack.push(pushStack[pushTemp++]);
            }
            // 出栈
            stack.pop();
            popTemp++;
            if (popTemp == popStack.length) {
                is = true;
            }
        }
        return is;
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public static class TreeNode {
        public int val;
        public TreeNode left, right;

        public TreeNode(int val) {
            this.val = val;
            this.left = this.right = null;
        }
    }

    public int reachNumber(int target) {
        // Write your code here
        target = Math.abs(target);
        int step = 1, pos = 0;
        while (pos < target) {
            pos += step;
            step++;
        }
        step--;
        if (pos == target)
            return step;
        pos -= target;
        if (pos % 2 == 0)
            return step;
        else if ((step + 1) % 2 == 1)
            return step + 1;
        else return step + 2;
    }

    public int houseRobber3(TreeNode root) {
        // write your code here
        return dfs(root, false);
    }

    private int dfs(TreeNode root, boolean flag) {
        if (root == null)
            return 0;
        if (!flag)
            return Math.max(root.val + dfs(root.left, true) + dfs(root.right, true), dfs(root.left, false) + dfs(root.right, false));
        return dfs(root.left, false) + dfs(root.right, false);
    }

    public List<TreeNode> generateTrees(int n) {
        return generate(1, n);
    }

    private ArrayList<TreeNode> generate(int start, int end) {
        ArrayList<TreeNode> rst = new ArrayList<TreeNode>();
        if (start > end) {
            rst.add(null);
            return rst;
        }
        for (int i = start; i <= end; i++) {
            ArrayList<TreeNode> left = generate(start, i - 1);
            ArrayList<TreeNode> right = generate(i + 1, end);
            for (TreeNode l : left) {
                for (TreeNode r : right) {
                    TreeNode root = new TreeNode(i);
                    root.left = l;
                    root.right = r;
                    rst.add(root);
                }
            }
        }
        return rst;
    }

    public static int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (s == null || s.length() == 0)
            return 0;
        int[] a = new int[126];
        int max = 0, len = s.length();
        int start = 0, end = 0;
        while (end < len) {
            a[s.charAt(end++) - ' ']++;
            int p = 0;
            for (int i = 0; i < 126; i++)
                if (a[i] != 0)
                    p++;
            if (p <= k)
                max = Math.max(max, end - start);
            if (p > k) a[s.charAt(start++) - 'a']--;
        }
        return max;
    }

    public static int maxSubmatrix(int[][] matrix) {
        // write your code here
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0)
            return 0;
        int lenX = matrix.length, lenY = matrix[0].length;
        int[][] dp = new int[lenX][lenY];
        int max = 0;
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++)
                if (j == 0)
                    dp[i][j] = matrix[i][0];
                else
                    dp[i][j] = dp[i][j - 1] + matrix[i][j];
        for (int j = 0; j < matrix[0].length; j++) {
            for (int i = 0; i < matrix.length; i++) {
                if (i != 0)
                    dp[i][j] = dp[i - 1][j] > 0 ? dp[i - 1][j] + dp[i][j] : dp[i][j];
                max = Math.max(max, dp[i][j]);
            }
        }
        return max;
    }

    public int getDistance(int[] p, int[] d) {
        if (p == null || p.length == 0)
            return 0;
        Queue<Integer> queue = new PriorityQueue();
        return 0;
    }

    public String fractionToDecimal(int numerator, int denominator) {
        if (denominator == 0 || numerator == 0)
            return "0";
        StringBuffer sb = new StringBuffer();
        if (numerator < 0 && denominator > 0 || numerator > 0 && denominator < 0)
            sb.append("-");
        numerator = Math.abs(numerator);
        denominator = Math.abs(denominator);
        long num = numerator;
        long denum = denominator;
        long val = num / denum;
        sb.append(String.valueOf(val));
        num = (num % denum) * 10;
        Map<Long, Integer> map = new HashMap<>();
        while (num != 0) {
            if (map.containsKey(num)) {

            }
        }
        return null;
    }


    public int coinChange(int[] coins, int amount) {
        int[] res = new int[amount + 1];
        for (int i = 0; i < res.length; i++)
            res[i] = amount + 1;
        for (int i = 1; i <= amount; i++) {

        }
        return 0;
    }

    /*
    最长的回文子数组
     */
    public static int longestPalindromeSub(String s) {
        if (s == null || s.length() == 0)
            return 0;
        int max = 1;
        boolean[][] res = new boolean[s.length()][s.length()];
        int count = 0;
        while (count < s.length()) {
            for (int i = 0; i < s.length() - count; i++)
                if (count == 0 || count == 1 && s.charAt(i) == s.charAt(i + 1) || s.charAt(i) == s.charAt(i + count) && res[i + 1][i + count - 1]) {
                    res[i][i + count] = true;
                    max = Math.max(max, count + 1);
                }
            count++;
        }
        return max;
    }

    /*
    最长的回文子串
     */
    private static int longestPalindromeSubseq(String str) {
        if (str == null || str.length() == 0)
            return 0;
        int[][] dp = new int[str.length()][str.length()];
        int max = 1;
        int count = 0;
        while (count < str.length()) {
            for (int i = 0; i < str.length() - count; i++) {
                if (count == 0)
                    dp[i][i + count] = 1;
                else if (count == 1)
                    dp[i][i + count] = str.charAt(i) == str.charAt(i + count) ? 2 : 1;
                else {
                    if (str.charAt(i) == str.charAt(i + count))
                        dp[i][i + count] = dp[i + 1][i + count - 1] + 2;
                    else
                        dp[i][i + count] = Math.max(dp[i + 1][i + count], dp[i][i + count - 1]);
                    max = Math.max(max, dp[i][i + count]);
                }
            }
            count++;
        }
        return max;
    }


    public int shortestDistance(int[][] maze, int[] start, int[] destination) {
        int[] dx = new int[]{0, 1, 0, -1};
        int[] dy = new int[]{1, 0, -1, 0};
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Queue<int[]> queue = new LinkedList<int[]>();
        Queue<Integer> counts = new LinkedList<>();
        int min = Integer.MAX_VALUE;
        ((LinkedList<int[]>) queue).add(start);
        ((LinkedList<Integer>) counts).add(0);
        visited[start[0]][start[1]] = true;
        int[] cur;
        int count, x, y, tmp;
        while (!queue.isEmpty()) {
            cur = queue.poll();
            count = counts.poll();
            for (int i = 0; i < 4; i++) {
                x = cur[0];
                y = cur[1];
                tmp = count;
                while (isValid(maze, x + dx[i], y + dy[i])) {
                    x += dx[i];
                    y += dy[i];
                    tmp++;
                }
                if (x == destination[0] && y == destination[1])
                    return tmp;
                if (!visited[x][y]) {
                    visited[x][y] = true;
                    ((LinkedList<int[]>) queue).add(new int[]{x, y});
                    ((LinkedList<Integer>) counts).add(tmp);
                }
            }
        }
        return -1;
    }

    private boolean isValid(int[][] maze, int x, int y) {
        int m = maze.length, n = maze[0].length;
        if (x < 0 || x > m || y < 0 || y > n)
            return false;
        return maze[x][y] == 0;
    }


    public static String nextClosestTime(String time) {
        if (time == null || time.length() == 0)
            return "";
        String[] a = time.split(":");
        int h = Integer.parseInt(a[0]);
        int m = Integer.parseInt(a[1]);
        String ret = "";
        A:
        for (int i = 0; i <= 24 * 60; i++) {
            if (++m == 60) {
                m = 0;
                if (++h == 24)
                    h = 0;
            }
            ret = String.format("%02d:%02d", h, m);
            for (char c : ret.toCharArray())
                if (time.indexOf(c) < 0)
                    continue A;
            return ret;
        }
        return ret;
    }

    /*
    lintcode
    caiziyouxi
     */
    public int getMoneyAmount(int n) {
        // write your code here
        int[][] dp = new int[n][n];
        int count = 0;
        while (count <= n) {
            for (int i = 0; i < n - count; i++) {
                if (count == 0)
                    dp[i][i + count] = 0;
                else if (count == 1)
                    dp[i][i + count] = i + count;
                else {
                    int tmp = 1;
                    while (tmp < count) {
                        dp[i][i + count] = Math.min(dp[i][i + count], Math.max(dp[i][i + tmp - 1], dp[i + tmp + 1][i + count]) + i + tmp + 1);
                        tmp++;
                    }
                }
            }
            count++;
        }
        return dp[0][n - 1];
    }

    /*
    贪心+DP
     */
    public static int maxB(int N) {
        int[] dp = new int[N];
        for (int i = 0; i < N; i++)
            if (i < 4)
                dp[i] = i + 1;
            else {
                int max = i + 1;
                for (int k = i - 3; k >= 0 && k >= i - 7; k--)
                    max = Math.max(max, dp[k] * (i - 2 - k));
                dp[i] = max;
            }
        return dp[N - 1];
    }

    public static boolean isReflected(int[][] points) {
        Map<Integer, List<Integer>> maps = new HashMap<>();
        for (int[] point : points) {
            if (maps.containsKey(point[1]))
                maps.get(point[1]).add(point[0]);
            else {
                List<Integer> list = new ArrayList<>();
                list.add(point[0]);
                maps.put(point[1], list);
            }
        }
        float sign = 0;
        boolean flag = true;
        for (List<Integer> list : maps.values()) {
            int size = list.size();
            Collections.sort(list);
            if (flag) {
                if (size % 2 == 1)
                    sign = list.get(size / 2) * 2;
                else
                    sign = list.get(0) + list.get(size - 1);
                flag = false;
            }
            for (int i = 0; i < size / 2; i++)
                if (list.get(i) + list.get(size - 1 - i) != sign)
                    return false;
        }
        return true;
    }

    public boolean cardGame(int[] cost, int[] damage, int totalMoney, int totalDamage) {
        int len = cost.length;
        if (len == 0 || totalMoney == 0)
            return false;
        int[][] dp = new int[len][totalMoney + 1];
        for (int i = 0; i <= totalMoney; i++)
            dp[0][i] = (i >= cost[0] ? damage[0] : 0);
        for (int i = 1; i < len; i++) {
            for (int j = 0; j <= totalMoney; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= cost[i])
                    dp[i][j] = Math.max(dp[i][j], damage[i] + dp[i - 1][j - cost[i]]);
            }
        }
        return dp[len - 1][totalMoney] >= totalDamage;
    }

    /*
    四个数计算能不能组成24点
     */
    public static boolean compute24(double[] nums) {
        List<Double> list = new ArrayList<>();
        for (double n : nums)
            list.add(n);
        return dfs(list);
    }

    static double eps = 1e-6;

    private static boolean dfs(List<Double> nums) {
        if (nums.size() == 1)
            return Math.abs(nums.get(0) - 24) < eps;
        int size = nums.size();
        double num1, num2;
        for (int i = 0; i < size - 1; i++) {
            num1 = nums.get(i);
            for (int j = i + 1; j < size; j++) {
                num2 = nums.get(j);
                double p1 = num1 + num2, p2 = num1 - num2, p3 = num2 - num1, p4 = num1 * num2, p5 = num1 / num2, p6 = num2 / num1;
                List<Double> next = new ArrayList<>();
                next.add(p1);
                next.add(p2);
                next.add(p3);
                next.add(p4);
                next.add(p5);
                next.add(p6);
                nums.remove(num1);
                nums.remove(num2);
                for (Double d : next) {
                    nums.add(d);
                    if (dfs(nums))
                        return true;
                    nums.remove(nums.size() - 1);
                }
                nums.add(i, num1);
                nums.add(j, num2);
            }
        }
        return false;
    }

    public static List<String> removeComments(String[] source) {
        List<String> list = new ArrayList<>();
        int res = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < source.length; i++) {
            String tmp = source[i];
            if (res != 0) {
                if (tmp.lastIndexOf("*/") != -1) {
                    if (tmp.lastIndexOf("*/") + 2 != tmp.length() || sb.length() != 0) {
                        sb.append(tmp.substring(tmp.lastIndexOf("*/") + 2, tmp.length()));
                        list.add(sb.toString());
                    }
                    res--;
                }
            } else {
                if (tmp.indexOf("//") != -1) {
                    if (tmp.indexOf("//") != 0)
                        list.add(tmp.substring(0, tmp.indexOf("//")));
                } else if (tmp.indexOf("/*") != -1) {
                    if (tmp.lastIndexOf("*/") != -1) {
                        if (tmp.indexOf("/*") != 0 || tmp.lastIndexOf("*/") + 2 != tmp.length())
                            list.add(tmp.substring(0, tmp.indexOf("/*")) + tmp.substring(tmp.lastIndexOf("*/") + 2, tmp.length()));
                    } else {
                        if (tmp.indexOf("/*") != 0)
                            sb.append(tmp.substring(0, tmp.indexOf("/*")));
                        res++;
                    }
                } else
                    list.add(tmp);
            }
        }
        return list;
    }

    /*
    all arrays is nums ,split to m
    一个数组，划分成m分，让最大的一份最小
     */
    public int splitArray(int[] nums, int m) {
        int[][] dp = new int[m][nums.length];
        dp[0][0] = nums[0];
        for (int i = 1; i < nums.length; i++)
            dp[0][i] = dp[0][i - 1] + nums[i];
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < nums.length; j++) {
                dp[i][j] = Integer.MAX_VALUE;
                int sum = nums[j];
                for (int k = j - 1; k >= 0; k--) {
                    dp[i][j] = Math.min(dp[i][j], Math.max(dp[i - 1][k], sum));
                    sum += nums[k];
                }
            }
        }
        return dp[m - 1][nums.length - 1];
    }


    /*
    exist was wrong
     */
    public List<Integer> numIslands(int n, int m, Point[] operators) {
        int[][] islands = new int[n][m];
        List<Integer> list = new ArrayList<>();
        if (operators == null || operators.length == 0)
            return list;
        for (Point operator : operators) {
            int x = operator.x;
            int y = operator.y;
            islands[x][y] = 1;
            if (x > 0 && islands[x - 1][y] == 1 || x < n - 1 && islands[x + 1][y] == '1' || y > 0 && islands[x][y - 1] == '1' || y < m - 1 && islands[x][y + 1] == '1') {
                list.add(list.get(list.size() - 1));
            } else {
                if (list.isEmpty())
                    list.add(1);
                else
                    list.add(list.get(list.size() - 1) + 1);
            }
        }
        return list;
    }


    public static List<Integer> numIslands2(int n, int m, Point[] operators) {
        List<Integer> res = new ArrayList<>();
        if (operators == null || operators.length == 0)
            return res;
        int cnt = 0;
        int[] roots = new int[n * m];
        int[][] dirs = new int[][]{{0, -1}, {-1, 0}, {0, 1}, {1, 0}};
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                roots[i * m + j] = -1;
        for (Point operator : operators) {
            int id = m * operator.x + operator.y;
            if (roots[id] == -1) {
                roots[id] = id;
                cnt++;
            }
            for (int[] dir : dirs) {
                int x = operator.x + dir[0], y = operator.y + dir[1], cur_id = m * x + y;
                if (x < 0 || x >= n || y < 0 || y >= m || roots[cur_id] == -1)
                    continue;
                int p = findRoot(roots, cur_id), q = findRoot(roots, id);
                if (p != q) {
                    roots[p] = q;
                    cnt--;
                }
            }
            res.add(cnt);
        }
        return res;
    }


    private static int findRoot(int[] root, int id) {
        return (root[id] == id) ? id : findRoot(root, root[id]);
    }


    public String removeKdigits(String num, int k) {
        int len = num.length();
        if (k >= len || len == 0)
            return "0";
        //栈顶始终是最大值
        Stack<Integer> stack = new Stack<>();
        stack.push(num.charAt(0) - '0');
        for (int i = 1; i < len; i++) {
            int now = num.charAt(i) - '0';
            while (!stack.isEmpty() && k > 0 && now > stack.peek()) {
                stack.pop();
                k--;
            }
            if (now != 0 || !stack.isEmpty())
                stack.push(now);
        }
        while (k > 0) {
            stack.pop();
            k--;
        }
        if (stack.isEmpty())
            return "0";
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty())
            sb.append(stack.pop());
        return sb.reverse().toString();
    }


    //单调栈
    public static boolean find132pattern(int[] nums) {
        int min = Integer.MIN_VALUE;
        Stack<Integer> stack = new Stack<>();
        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums[i] < min)
                return true;
            while (!stack.isEmpty() && nums[i] > stack.peek())
                min = stack.pop();
            stack.push(nums[i]);
        }
        return false;
    }


    public static int[] nextGreaterElements(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
                res[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        int size = stack.size();
        while (size != 0) {
            for (int i = 0; i < nums.length; i++) {
                while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
                    res[stack.pop()] = nums[i];
                }
            }
            size--;
        }
        while (!stack.isEmpty()) {
            res[stack.pop()] = -1;
        }
        return res;
    }

    public static int[][] reconstructQueue(int[][] people) {
        if (people == null || people.length == 0)
            return new int[0][0];
        Arrays.sort(people, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] == o2[0] ? o1[1] - o2[1] : o2[0] - o1[0];
            }
        });
        List<int[]> list = new ArrayList<>();
        for (int[] i : people) {
            list.add(i[1], i);
        }
        return list.toArray(new int[list.size()][]);
    }


    private int[] nums;
    private int len;
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public Test(int[] nums) {
        this.nums = nums;
        len = nums.length;
    }

    /**
     * Resets the array to its original configuration and return it.
     */
    public int[] reset() {
        return nums;
    }

    /**
     * Returns a random shuffling of the array.
     */
    public int[] shuffle() {
        int[] newNum = nums.clone();
        for (int i = 0; i < len; i++) {
            int j = random.nextInt(i, len);
            int temp = newNum[i];
            newNum[j] = newNum[i];
            newNum[i] = temp;
        }
        return newNum;
    }


    static String[] less_20 = {"", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelf", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "ninteen"};
    static String[] more_than_20 = {"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

    public static String convertWords(int number) {
        if (number == 0)
            return "zero";
        return helper(number).trim();
    }

    private static String helper(int number) {
        if (number < 20)
            return less_20[number];
        if (number < 100)
            return more_than_20[number / 10 - 2] + " " + helper(number % 10);
        if (number < 1000)
            return less_20[number / 100] + " hundred  " + helper(number % 100);
        if (number < 1000000)
            return helper(number / 1000) + " thousand " + helper(number % 1000);
        if (number < 1000000000)
            return helper(number / 1000000) + " million " + helper(number % 1000000);
        return helper(number / 1000000000) + " billion " + helper(number % 1000000000);
    }

    private static String[][] getData(File file, int ignoreRows) throws IOException {
        List<String[]> result = new ArrayList<>();
        int rowSize = 0;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFCell cell = null;
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
                HSSFRow row = st.getRow(rowIndex);
                if (row == null)
                    continue;
                int tempRowSize = row.getLastCellNum() + 1;
                if (tempRowSize > rowSize)
                    rowSize = tempRowSize;
                String[] values = new String[rowSize];
                Arrays.fill(values, "");
                boolean hasValue = false;
                for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
                    String value = "";
                    cell = row.getCell(columnIndex);
                    if (cell != null) {
                        // 注意：一定要设成这个，否则可能会出现乱码
                        //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    if (date != null) {
                                        value = new SimpleDateFormat("yyyy-MM-dd")
                                                .format(date);
                                    } else {
                                        value = "";
                                    }
                                } else {
                                    value = new DecimalFormat("0").format(cell
                                            .getNumericCellValue());
                                }
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA:
                                // 导入时如果为公式生成的数据则无值
                                if (!cell.getStringCellValue().equals("")) {
                                    value = cell.getStringCellValue();
                                } else {
                                    value = cell.getNumericCellValue() + "";
                                }
                                break;
                            case HSSFCell.CELL_TYPE_BLANK:
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                value = "";
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                value = (cell.getBooleanCellValue() == true ? "Y"

                                        : "N");
                                break;
                            default:
                                value = "";
                        }
                    }
                    if (columnIndex == 0 && value.trim().equals("")) {
                        break;
                    }
                    values[columnIndex] = rightTrim(value);
                    hasValue = true;
                }
                if (hasValue)
                    result.add(values);
            }
        }
        in.close();
        String[][] returnArray = new String[result.size()][rowSize];
        for (int i = 0; i < returnArray.length; i++)
            returnArray[i] = (String[]) result.get(i);
        return returnArray;
    }

    private static String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20) {
                break;
            }
            length--;
        }
        return str.substring(0, length);
    }

    static class CheckPrimeNumber extends Thread {
        int start;
        int end;
        private static int threadPoolSize = 4;
        private static int numberParts = 4;
        private static AtomicInteger integer = new AtomicInteger();

        public boolean isPrimeNumber(int number) {
            if (number < 1)
                return false;
            for (int i = 2; i <= Math.sqrt(number); i++) {
                if (number % i == 0)
                    return false;
            }
            return true;
        }

        public int getPrimeNumbers(int start, int end) {
            for (int i = start; i <= end; i++) {
                if (!isPrimeNumber(i)) {
                    integer.getAndIncrement();
                }
            }
            System.out.print(integer.get() + " ");
            return integer.get();
        }

        public void sumPrimeNumber(int number) {
            int nums = number / numberParts;
            List<Callable<Integer>> callableList = new ArrayList<>();
            for (int i = 0; i < numberParts; i++) {
                int start = i * number + 1;
                int end = (numberParts - i == 1) ? number : start + number - 1;
                callableList.add(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return getPrimeNumbers(start, end);
                    }
                });
            }
            try {
                ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
                executor.invokeAll(callableList, 10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Point {
        int x;
        int y;

        Point() {
            x = 0;
            y = 0;
        }

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            Point point = (Point) obj;
            return this.x == point.x && this.y == point.y;
        }
    }

    private double check(String temp, long x1, long x2, long y1, long y2) {
        String[] strs = temp.split(";");
        double min = Long.MAX_VALUE;
        for (String str : strs) {
            String[] s = str.split(",");
            long sx = Long.parseLong(s[0]);
            long sy = Long.parseLong(s[1]);
            double m = Math.pow((x1 - sx) * (x1 - sx) + (y1 - sy) * (y1 - sy), 0.5);
            double n = Math.pow((x2 - sx) * (x2 - sx) + (y2 - sy) * (y2 - sy), 0.5);
            min = Math.min(min, m * n);
        }
        return min;
    }

    public static int minDeletionSize(String[] A) {
        int N = A.length, W = A[0].length();
        int ans = 0;
        String[] cur = new String[N];
        for (int j = 0; j < W; j++) {
            String[] cur2 = Arrays.copyOf(cur, N);
            for (int i = 0; i < N; i++)
                cur2[i] += A[i].charAt(j);
            if (isSorted(cur2))
                cur = cur2;
            else
                ans++;
        }
        return ans;
    }

    private static boolean isSorted(String[] A) {
        for (int i = 0; i < A.length - 1; i++)
            if (A[i].compareTo(A[i + 1]) > 0)
                return false;
        return true;
    }

    public int minDeletionSize1(String[] A) {
        if (A == null || A.length == 0)
            return 0;
        int count = 0, index = 0, size = A.length, len = A[0].length();
        a:
        while (index < len) {
            for (int i = 1; i < len; i++)
                if (A[i].charAt(index) < A[i - 1].charAt(index)) {
                    count++;
                    index++;
                    continue a;
                }
            index++;
        }
        return count;
    }

    public static List<Integer> findMinHeightTrees(int n, int[][] edges) {
        if (n == 1)
            return Collections.singletonList(0);
        List<Integer> leaves = new ArrayList<>();
        List<Set<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            adj.add(new HashSet<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        for (int i = 0; i < n; i++)
            if (adj.get(i).size() == 1)
                leaves.add(i);

        while (n > 2) {
            n -= leaves.size();
            List<Integer> newLeaves = new ArrayList<>();
            for (int i : leaves) {
                int t = adj.get(i).iterator().next();
                adj.get(t).remove(i);
                if (adj.get(t).size() == 1)
                    newLeaves.add(t);
            }
            leaves = newLeaves;
        }
        return leaves;
    }

    public static String getImageStr(String filepath) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(filepath);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    public boolean Base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) {
            // 图像数据为空
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int maximumSum(int[] arr) {
        int[][] dp = new int[arr.length][2];
        dp[0][0] = arr[0];
        dp[0][1] = -100000;
        int len = arr.length;
        for (int i = 1; i < len; i++) {
            dp[i][0] = Math.max(arr[i], dp[i - 1][0] + arr[i]);
            dp[i][1] = Math.max(dp[i - 1][0], arr[i] + dp[i - 1][1]);
        }
        int max = -100000;
        for (int i = 0; i < len; i++) {
            max = Math.max(max, Math.max(dp[i][0], dp[i][1]));
        }
        return max;
    }

    class Codec {
        // Encodes a tree to a single string.
        public String serialize(TreeNode root) {
            if (root == null)
                return "";
            StringBuffer sb = new StringBuffer();
            Queue<TreeNode> queue = new LinkedList<>();
            queue.add(root);
            TreeNode node;
            while (!queue.isEmpty()) {
                int count = queue.size();
                while (count < 0) {
                    node = queue.poll();
                    count--;
                    if (node == null)
                        sb.append('#');
                    else {
                        sb.append(String.valueOf(node.val) + "!");
                        queue.add(node.left);
                        queue.add(node.right);
                    }
                }
            }
            return sb.toString();
        }
    }

    private static boolean isRunning(String fileLocalPath) {
        String os_name = System.getProperty("os.name");
        String path = null;
        if (os_name.indexOf("Windows") > -1)
            //如果是Windows操作系统
            path = System.getProperty("user.home") + System.getProperty("file.separator");
        else
            path = "/usr/temp/";
        System.out.println(os_name + "\n" + path);
        return false;
    }

    public static int myAtoi(String str) {
        if (str.isEmpty())
            throw new NullPointerException();
        int flag = 1, i = 0, tmp;
        long result = 0;
        while (i < str.length() && str.charAt(i) == ' ')
            i++;
        if (i == str.length())
            return 0;
        if (str.charAt(i) == '+' || str.charAt(i) == '-') {
            flag = str.charAt(i) == '+' ? 1 : -1;
            i++;
        }
        while (i < str.length() && str.charAt(i) >= '0' && str.charAt(i) <= '9') {
            tmp = str.charAt(i++) - '0';
            if (tmp > 9 || tmp < 0)
                throw new NumberFormatException();
            result = 10 * result + tmp;
            if (result > Integer.MAX_VALUE) {
                throw new NumberFormatException();
            }
        }
        return (int) (result * flag);
    }

    public static int[] solution(int[][] tree1, int[][] tree2) {
        Queue<int[]> queue1 = new LinkedList<>();
        Queue<int[]> queue2 = new LinkedList<>();
        List<Integer> list = new ArrayList<>();
        queue1.add(tree1[0]);
        queue2.add(tree2[0]);
        int[] tmp1, tmp2;
        while (queue1.isEmpty() && queue2.isEmpty()) {
            int size1 = queue1.size();
            int size2 = queue2.size();
            if (size2 == 0) {
                while (size1 != 0) {
                    tmp1 = queue1.poll();
                    queue1.add(tmp1 == null || tmp1[0] == 0 ? null : tree1[tmp1[0] - 1]);
                    queue1.add(tmp1 == null || tmp1[1] == 0 ? null : tree1[tmp1[1] - 1]);
                    list.add(tmp1[2]);
                    size1--;
                }
            } else if (size1 == 0) {
                while (size2 != 0) {
                    tmp2 = queue2.poll();
                    queue2.add(tmp2 == null || tmp2[0] == 0 ? null : tree2[tmp2[0] - 1]);
                    queue2.add(tmp2 == null || tmp2[1] == 0 ? null : tree2[tmp2[1] - 1]);
                    list.add(tmp2[2]);
                    size2--;
                }
            } else {
                while (size1 != 0) {
                    tmp1 = queue1.poll();
                    tmp2 = queue2.poll();
                    queue1.add(tmp1 == null || tmp1[0] == 0 ? null : tree1[tmp1[0] - 1]);
                    queue1.add(tmp1 == null || tmp1[1] == 0 ? null : tree1[tmp1[1] - 1]);
                    queue2.add(tmp2 == null || tmp2[0] == 0 ? null : tree2[tmp2[0] - 1]);
                    queue2.add(tmp2 == null || tmp2[1] == 0 ? null : tree2[tmp2[1] - 1]);
                    list.add(tmp1[2] + tmp2[2]);
                    size1--;
                }
            }
        }
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }


    public TreeNode bstSwappedNode(TreeNode root) {
        // write your code here
        if (root == null)
            return null;
        int val = root.val;
        if (root.left != null && root.right == null && root.left.val > val) {
            root.val = root.left.val;
            root.left.val = val;
        } else if (root.right != null && root.left == null && root.right.val < val) {
            root.val = root.right.val;
            root.right.val = val;
        } else if (root.right != null && root.left != null) {
            int left = root.left.val;
            int right = root.right.val;
            if (left < val && val < right) {
                bstSwappedNode(root.left);
                bstSwappedNode(root.right);
            } else if (right < val && val < left) {
                root.left.val = right;
                root.right.val = left;
                //说明出现问题的是根节点
            } else if (val > left && val > right && left < right) {
                TreeNode tmp = findRight(root.right, right);
                root.val = tmp.val;
                tmp.val = val;
                //说明出现问题的是根节点
            } else if (val < left && val < right && left < right) {
                TreeNode tmp = findLeft(root.left, left);
                root.val = tmp.val;
                tmp.val = val;
                //说明出现问题的是右节点
            } else if (val > left && val > right && left > right) {
                TreeNode tmp = findLeft(root.left, val);
                root.right.val = tmp.val;
                tmp.val = right;
                //说明出现问题的是左节点
            } else if (val < left && val < right && left > right) {
                TreeNode tmp = findRight(root.right, val);
                root.left.val = tmp.val;
                tmp.val = left;
            }
        }
        return root;
    }

    private TreeNode findLeft(TreeNode root, int val) {
        if (root == null)
            return null;
        TreeNode node = findLeft(root.left, val);
        if (node != null)
            return node;
        if (root.val >= val)
            return root;
        return findLeft(root.right, val);
    }

    private TreeNode findRight(TreeNode root, int val) {
        if (root == null)
            return null;
        TreeNode node = findRight(root.right, val);
        if (node != null)
            return node;
        if (root.val <= val)
            return root;
        return findRight(root.left, val);
    }

    static class MyCallable implements Callable {

        String str;

        MyCallable(String str) {
            this.str = str;
        }

        @Override
        public Object call() throws Exception {
            return str;
        }
    }

    public static void thread() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1000);
        List<Future> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            Callable call = new MyCallable("OK!!" + i);
            Future future = pool.submit(call);
            list.add(future);
        }
        pool.shutdown();
        for (Future f : list)
            System.out.println("res:" + f.get().toString());
    }

    public int[] constructArr(int[] a) {
        int len = a.length;
        int[] left = new int[len], right = new int[len], res = new int[len];
        for (int i = 0; i < len; i++)
            if (i == 0) {
                left[i] = a[i];
                right[len - 1 - i] = a[len - 1 - i];
            } else {
                left[i] = left[i - 1] * a[i];
                right[len - 1 - i] = right[len - i] * a[len - 1 - i];
            }
        for (int i = 0; i < len; i++)
            if (i == 0)
                res[i] = right[i + 1];
            else if (i == len - 1)
                res[i] = left[i - 1];
            else
                res[i] = right[i + 1] * left[i - 1];
        return res;
    }

    public int countDigitOne(int n) {
        int count = 0;
        long i = 1;        // 从个位开始遍历到最高位
        while (n / i != 0) {
            long high = n / (10 * i);  // 高位
            long cur = (n / i) % 10;   // 当前位
            long low = n - (n / i) * i;
            if (cur == 0) {
                count += high * i;
            } else if (cur == 1) {
                count += high * i + (low + 1);
            } else {
                count += (high + 1) * i;
            }
            i = i * 10;
        }
        return count;
    }

    public int translateNum(int num) {
        String s = String.valueOf(num);
        int len = s.length();
        int[] dp = new int[len + 1];
        for (int i = 0; i <= len; i++)
            if (i < 2)
                dp[i] = 1;
            else {
                int n = Integer.valueOf(s.substring(i - 2, i));
                dp[i] = dp[i - 1] + (n <= 25 && n >= 10 ? dp[i - 2] : 0);
            }
        return dp[len];
    }

    public int majorityElement(int[] nums) {
        int key = nums[0];
        int count = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != key) count--;
            else count++;
            if (count < 0) {
                key = nums[i];
                count = 0;
            }
        }
        count = 0;
        for (int num : nums)
            if (num == key)
                count++;
        return count > nums.length / 2 ? key : -1;
    }

    class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    public Node copyRandomList(Node head) {
        if (head == null)
            return null;
        Map<Node, Node> map = new HashMap<>();
        Node cur = head;
        while (cur != null)
            map.put(cur, new Node((cur.val)));
        cur = head;
        while (cur != null) {
            map.get(cur).next = map.get(cur);
            map.get(cur).random = map.get(cur.random);
        }
        return map.get(head);
    }

    public static void main(String[] args) {

    }

}

class JvmThread {
    public static void main(String[] args) {
        new Thread(() -> {
            List<byte[]> list = new ArrayList<byte[]>();
            while (true) {
                System.out.println(new Date().toString() + Thread.currentThread() + "==");
                byte[] b = new byte[1024 * 1024 * 1];
                list.add(b);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();        // 线程二
        new Thread(() -> {
            while (true) {
                System.out.println(new Date().toString() + Thread.currentThread() + "==");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

interface IBlogService {
    void writeBlog();
}

class BlogService implements IBlogService {
    @Override
    public void writeBlog() {
        System.out.println("执行目标对象的方法");
    }
}

class CglibBlogFactory implements MethodInterceptor {
    private Object target;

    public CglibBlogFactory(Object target) {
        this.target = target;
    }

    //给目标对象设置一个代理对象
    public Object getProxyInstance() {
        //1.CGLIB enhancer 增强类对象
        Enhancer en = new Enhancer();
        //设置增强类型
        en.setSuperclass(target.getClass());
        //3.定义代理逻辑对象为当前对象，要求当前对象实现MethodInterceptor方法
        en.setCallback(this);
        //4.生成并返回代理对象
        return en.create();
    }

    /**
     * 代理逻辑方法
     *
     * @param o           代理对象
     * @param method      方法
     * @param objects     方法参数
     * @param methodProxy 方法代理
     * @return 代理逻辑返回
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("start writing...");
        Object returnValue = method.invoke(target, objects);
        System.out.println("end writing...");
        return returnValue;
    }


    public static void main(String[] args) {
        IBlogService target = new BlogService();
        //代理对象
        IBlogService proxy = (IBlogService) new CglibBlogFactory(target).getProxyInstance();
        //执行代理对象的方法
        proxy.writeBlog();
    }
}

//JDK动态代理
class JdkBlogProxyFactory {
    private Object target;

    public JdkBlogProxyFactory(Object target) {
        this.target = target;
    }

    public Object newInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    System.out.println(1234);
                    Object o = method.invoke(target, args);
                    return o;
                });
    }

    public static void main(String[] args) throws InterruptedException {
        IBlogService target = new BlogService();
        IBlogService proxy = (IBlogService) new JdkBlogProxyFactory(target).newInstance();
        System.out.println("123");
        Thread.sleep(1000);
        proxy.writeBlog();
    }
}

class Main1 {
    public static int solutions(int[] datas, int start, int end, int target) {
        int res = 0;
        for (int i = start - 1; i < end - 1; i++)
            if (target == datas[i])
                res++;
        return res;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int count = scan.nextInt();
        scan.nextLine();
        int[] datas = new int[count];
        for (int i = 0; i < count; i++)
            datas[i] = scan.nextInt();
        scan.nextLine();
        int number = scan.nextInt();
        scan.nextLine();
        int[][] tmp = new int[number][3];
        int[] res = new int[3];
        for (int i = 0; i < number; i++) {
            tmp[i][0] = scan.nextInt();
            tmp[i][1] = scan.nextInt();
            tmp[i][2] = scan.nextInt();
            res[i] = solutions(datas, tmp[i][0], tmp[i][1], tmp[i][2]);
        }
        for (int i = 0; i < res.length; i++)
            System.out.println(res[i]);
    }
}


class TicketWindowRunnable implements Runnable {


    private int index = 1;
    private final static int MAX = 50000;

    @Override
    public void run() {
        while (index <= MAX)
            System.out.println(Thread.currentThread() + "的号码是：" + (index++));
    }

    public static void main(String[] args) {
        final TicketWindowRunnable task = new TicketWindowRunnable();
        Thread windowThread1 = new Thread(task, "一号窗口");
        Thread windowThread2 = new Thread(task, "二号窗口");
        Thread windowThread3 = new Thread(task, "三号窗口");
        Thread windowThread4 = new Thread(task, "四号窗口");
        windowThread1.start();
        windowThread2.start();
        windowThread3.start();
        windowThread4.start();
    }
}
