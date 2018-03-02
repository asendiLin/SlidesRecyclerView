
> 列表的条目侧滑删除可以说是最熟悉不过的了，它的实现方法也有很多，这次我给大家带来的是基于RecyclerView实现的。

先看看效果图：

![这里写图片描述](http://img.blog.csdn.net/20180302221838600?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQV9zZW5keQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### 大概思路为：
 - 重写RecyclerView的onTochEvent方法
 - 点击时，获取点击的位置，根据位置（坐标）去获取点击的条目对应的View，顺便获取条目的索引，服务于后面删除数据的逻辑。
 - 滑动时，跟着手指滑动，但要防止超出边界的处理
 - 松开时，获取手势、滑动的速度和条目View所在的位置，并作出对应的处理
### 根据以下的问题来对思路的展开
#### 1.如何获取当前点击到的条目？

```
//在按下去的时（ACTION_DOWN）获取
 View view = this.findChildViewUnder(startX, startY);//获取点击到的View
                    mViewHolder = (SlidesAdapter.SlidesViewHolder) this.findContainingViewHolder(view);//获取ViewHolder
 position = mViewHolder.getAdapterPosition();//获取当前条目的所有
 mItemView = mViewHolder.ContentView;
 deleteView = mViewHolder.deleteView;//删除的View，这里是一张图片
 deleteViewWidth = funView.getWidth();//获取侧滑出的功能图片的宽

```

#### 2.如何处理跟着手指滑动？
可以使用View的ScrollBy(int x,int y)或者View的ScrollTo(int x,int y)方法,其实ScrollBy方法里边调用的是ScrollTo方法，它的源代码：

```
 public void scrollBy(int x, int y) {
        scrollTo(mScrollX + x, mScrollY + y);
    }
```
所以为了方便，在实现View随着手指滑动时，使用ScrollBy。
这里用图解释下mScrollX和mScrollY，其中粉红色代表View的内容：
![这里写图片描述](http://img.blog.csdn.net/20180213230903403?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQV9zZW5keQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

然后还有防止把View滑动出边界，这里限制的边界是左边滑动不能超过删除图标的宽度，右边不能超出原来的边界（也就是在没有打开删除图标时，条目的内容不能向右滑动）

```
//滑动时的逻辑（ACTION_MOVE）
                int deltaX = mLastX - startX;//<0:向右滑动、>0:向左滑动
                int deltaY = mLastY - startY;
                int scrollX = mItemView.getScrollX();//当前条目内容View的mScroller

                if (Math.abs(deltaX) < Math.abs(deltaY)) {//处于上下滑动，交给RecyclerView处理
                    break;
                }

                if (deltaX > 0) {//向左滑动
                    if (deltaX + scrollX >= deleteViewWidth) {//防止向左滑动出界
                        mItemView.scrollTo(deleteViewidth, 0);
                        return true;//继续接收滑动事件
                    }
                } else {//向右滑动
                    if (scrollX < 0) {
                        mItemView.scrollTo(0, 0);
                        return true;
                    }
                }

                mItemView.scrollBy(deltaX, 0);
                break;
```

#### 3.如何松开手时获取手势、速度？
VelocityTracker：它用于获取手指滑动的速度，使用方法为：

```
 VelocityTracker mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);//表示1S内手指划过的像素
        mVelocityTracker.getXVelocity();//获取水平方向的速度(从右往左为负值，相反为正值)
        mVelocityTracker.clear();
        mVelocityTracker.recycle();
```

#### 4.如何有弹性的滑动？
Scroller对象是弹性滑动对象，但它本身无法让View弹性滑动，需要与computeScroll()方法配合使用：

```
Scroller Scroller = new Scroller(context);
mScroller.startScroll(mItemView.getScrollX(), 0, -deleteViewWidth, 0);

 @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mItemView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } 
 }
```
#### 条目滑动的状态

```
enum STATES {
        CLOSE,//删除图标是关闭的
        OPEN,//删除图标是打开的
        TO_OPEN,//删除图标要去关闭
        TO_CLOSE//删除图标要去打开
    }
```
#### 核心代码

```
  @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mItemView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            } else {
            if (states == STATES.TO_OPEN) {
                states = STATES.OPEN;
                deleteView.setVisibility(VISIBLE);
                //显示时添加动画，可自己定义
                Animation animator = AnimationUtils.loadAnimation(getContext(),
                        R.anim.fun_anim);
                deleteView.startAnimation(animator);

            } else if (states == STATES.TO_CLOSE) {
                states = STATES.CLOSE;
                deleteView.setVisibility(INVISIBLE);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent e) {
        mVelocityTracker.addMovement(e);
        int startX = (int) e.getX();
        int startY = (int) e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (states == STATES.CLOSE) {//处于关闭状态
                    View view = this.findChildViewUnder(startX, startY);
                    mViewHolder = (SlidesAdapter.SlidesViewHolder) this.findContainingViewHolder(view);

                    mItemView = mViewHolder.ContentView;
                    deleteView = mViewHolder.deleteView;
                    deleteViewWidth = deleteView.getWidth();//获取侧滑出的功能图片的宽

                } else if (states == STATES.OPEN) {//处于打开状态
                    mScroller.startScroll(mItemView.getScrollX(), 0, -deleteViewWidth, 0);//滑动到关闭
                    deleteView.setVisibility(INVISIBLE);
                    invalidate();
                    states = STATES.CLOSE;
                    return false;
                } else {//其他状态
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                int deltaX = mLastX - startX;//<0:向右滑动、>0:向左滑动
                int deltaY = mLastY - startY;
                int scrollX = mItemView.getScrollX();//当前条目内容View的mScroller

                if (Math.abs(deltaX) < Math.abs(deltaY)) {//处于向上下滑动时，交给RecyclerView处理
                    break;
                }

                if (deltaX > 0) {//向左滑动
                    if (deltaX + scrollX >= deleteViewWidth) {//防止向左滑动出界
                        mItemView.scrollTo(deleteViewWidth, 0);
                        return true;
                    }
                } else {//向右滑动
                    if (scrollX < 0) {
                        mItemView.scrollTo(0, 0);
                        return true;
                    }
                }

                mItemView.scrollBy(deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                int upScrollX = mItemView.getScrollX();
                mVelocityTracker.computeCurrentVelocity(100);
                int xVleocity = (int) mVelocityTracker.getXVelocity();
                int deltaScrollX = 0;

//                滑动速度大于50
                if (Math.abs(xVleocity) >= 50) {
                    //判断手势
                    if (xVleocity >= 50) {//从左向右
                        deltaScrollX = -upScrollX;
                        states = STATES.TO_CLOSE;//关闭趋势

                    } else if (xVleocity <= -50) {//从右向左
                        deltaScrollX = deleteViewWidth - upScrollX;
                        states = STATES.TO_OPEN;//打开趋势
                    }
                } else {
                    if (upScrollX >= deleteViewWidth / 2) {
                        //当向左滑动超过删除View的宽度的1/2时，说明要打开
                        deltaScrollX = deleteViewWidth - upScrollX;
                        states = STATES.TO_OPEN;
                    } else {
                        //当向左滑动小于删除View的宽度的1/2时，说明要打开
                        deltaScrollX = -upScrollX;
                        states = STATES.TO_CLOSE;
                    }
                }

                mScroller.startScroll(upScrollX, 0, deltaScrollX, 0, 500);

                invalidate();
                mVelocityTracker.clear();

                startX = 0;//防止下次无法对View的滑动

                break;

        }
        mLastX = startX;
        mLastY = startY;
        return super.onTouchEvent(e);
    }
```
