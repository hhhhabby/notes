如何设计，实现表格的 3 px 实线蓝色边框 #flashcard
1. Table{
W h}
2. Table, td, th{
Border: 三件套
3. Border-collapse: collapse 
}
<!--ID: 1752804857332-->


写出 border 的不简写，简写，消除重叠 #flashcard
Border-left; border-right; border-top; border-bottom;  
Boder: 1 px dashed/dottded/solid blue;  
Border-collapse: collapse;
<!--ID: 1752804857368-->


如何利用 padding 实现文字缩进效果？ #flashcard
 可以通过 `padding-left`（左内边距）为块级元素设置文字缩进，例如段落文本。
	  - 示例：`p { padding-left: 2em; }` 表示段落中每一行文字（包括第一行）都会向右缩进 2 个字符宽度（`em` 相对于当前字体大小），类似 word 中的“左缩进”功能。
	  - 与 `text-indent` 的区别：`text-indent` 仅缩进第一行文字，而 `padding-left` 会让所有行统一右移，适合需要整体缩进的场景（如引用文本、列表项内容对齐）。
<!--ID: 1752804857375-->


如何设置块状元素里面的文字和图片垂直和水平居中？ #flashcard
	方法一(pm)：.box { width: 500px; margin: 0 auto; }
	方法二（pm适用于调节单个盒子）：合理设置 padding（左右） 和 margin （上下）。
	方法三：Test-align: center。
	水平垂直居中：
	方法一（高度）：给块状元素设 `line-height` 等于 `height`
	方法二：多行或复杂内容（含图片等）：可用 `flex` 布局
<!--ID: 1752804857382-->



如何清除内外边距？什么时候需要它？ #flashcard
想要自己设计中间水平居中的盒子，先消除默认边距干扰布局
* {
Pading：0;
Margin：0;
 }
<!--ID: 1752804857389-->




综合案例
1. 如何修改网页背景颜色？ #flashcard
修改 body 的 bgc
<!--ID: 1752804857396-->



2. 如何实现居中四个商品？ #flashcard
	 1 大 div（宽高+类名） 四个小 div（小宽高+类名）..................................
	2 小 div 图片，必须指定宽为 100%.
	3 插入文字.
	4 指定 padding 左右和 margin-top.
<!--ID: 1752804857404-->





如何不会撑开盒子？ #flashcard
```html
  overflow: hidden;   /* 防止内容撑破盒子（溢出部分隐藏） */
```
<!--ID: 1752804857410-->



文字为什么一定要指定高度？（虽然文字本身就有高度） #flashcard
格式对齐。而不是只是排列文字
<!--ID: 1752804857417-->


5. 标签的分类使用 #flashcard
名字/标签用 h
文字用 p
学会强转
<!--ID: 1752804857424-->


如何实现两个盒子拼接在一起的效果。 #flashcard
大盒子。然后给里面的元素加 border-bottom.
<!--ID: 1752804857431-->

阴影 #flashcard
	 Box-shadow: 10 px 10 px 20 px 6 px rgba (0, 0, 0, .3);
<!--ID: 1753170364676-->

