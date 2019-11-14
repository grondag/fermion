package grondag.fermion.gui.control;

public interface RenderBounds<T extends RenderBounds<T>>
{
	T offset(double x, double y);

	T scale(double left, double top, double width, double height);

	double left();
	double width();
	double top();
	double height();
	double right();
	double bottom();

	public static abstract class AbstractRectRenderBounds
	{
		public final double left;
		public final double top;
		public final double width;
		public final double height;

		public AbstractRectRenderBounds (double left, double top, double width, double height)
		{
			this.left = left;
			this.top = top;
			this.height = height;
			this.width = width;
		}

		public AbstractRectRenderBounds (double left, double top, double size)
		{
			this(left, top, size, size);
		}
		public double left() { return left; }
		public double top() { return top; }
		public double width() { return width; }
		public double height() { return height; }
		public double right() { return left + width; }
		public double bottom() { return top + height; }

		public boolean contains(double x, double y)
		{
			return !(x < left || x > right() || y < top || y > bottom());
		}
	}

	public static class RectRenderBounds extends AbstractRectRenderBounds implements RenderBounds<RectRenderBounds>
	{
		public RectRenderBounds(double left, double top, double width, double height)
		{
			super(left, top, width, height);
		}

		public RectRenderBounds(double left, double top, double size)
		{
			super(left, top, size);
		}

		@Override
		public RectRenderBounds offset(double x, double y)
		{
			return new RectRenderBounds(left + x, top + y, width, height);
		}

		@Override
		public RectRenderBounds scale(double left, double top, double width, double height)
		{
			return new RectRenderBounds(left, top, width, height);
		}
	}

	public static abstract class AbstractRadialRenderBounds extends AbstractRectRenderBounds
	{
		public final double centerX;
		public final double centerY;
		public final double radius;


		public AbstractRadialRenderBounds(double centerX, double centerY, double radius)
		{
			super(centerX - radius, centerY - radius, radius * 2);
			this.centerX = centerX;
			this.centerY = centerY;
			this.radius = radius;
		}

		public double centerX() { return centerX; }
		public double centerY() { return centerY; }
		public double radius() { return radius; }

	}

	public static class RadialRenderBounds extends AbstractRadialRenderBounds implements RenderBounds<RadialRenderBounds>
	{
		private RadialRenderBounds innerBounds;

		public RadialRenderBounds(double centerX, double centerY, double radius)
		{
			super(centerX, centerY, radius);
		}

		public RadialRenderBounds innerBounds()
		{
			if(innerBounds == null)
			{
				innerBounds = new RadialRenderBounds(centerX, centerY, radius / 2);
			}
			return innerBounds;
		}

		@Override
		public RadialRenderBounds offset(double x, double y)
		{
			return new RadialRenderBounds(centerX + x, centerY + y, radius);
		}

		@Override
		public RadialRenderBounds scale(double left, double top, double width, double height)
		{
			return new RadialRenderBounds(left + width / 2, top + height / 2, width / 2);
		}
	}

	//    public static class PowerRenderBounds extends AbstractRadialRenderBounds implements RenderBounds<PowerRenderBounds>
	//    {
	//        public final RectRenderBounds gainLossTextBounds;
	//        public final RectRenderBounds energyTextBounds;
	//        public final RectRenderBounds energyLevelBounds;
	//
	//        public PowerRenderBounds(double centerX, double centerY, double radius)
	//        {
	//            super(centerX, centerY, radius);
	//            this.gainLossTextBounds = new RectRenderBounds(left(), centerY - radius * 0.34, radius * 2, radius * 0.36 );
	//            this.energyTextBounds = new RectRenderBounds(left(), centerY + radius * 0.48, width(), radius * 0.48);
	//            this.energyLevelBounds = new RectRenderBounds(left(), centerY + radius / 10, width(), radius / 3);
	//        }
	//
	//        @Override
	//        public PowerRenderBounds offset(double x, double y)
	//        {
	//            return new PowerRenderBounds(this.centerX + x, this.centerY + y, this.radius);
	//        }
	//
	//        @Override
	//        public PowerRenderBounds scale(double left, double top, double width, double height)
	//        {
	//            return new PowerRenderBounds(left + width / 2, top + height / 2, width / 2);
	//        }
	//    }
}