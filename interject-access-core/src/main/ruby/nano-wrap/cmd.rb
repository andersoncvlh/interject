require 'rubygems'
require 'liquid'

# https://github.com/gkovacs/pdfocr/pull/1/files
def shell_escape(s)
    "'" + s.gsub("'", "'\\''") + "'"
end


phantom_render = "phantomjs rasterise.js {{in_url}} {{out_file.png}}"

OR

phantom_render = "phantomjs rasterise.js %{in_url} %{out_file.png}"




@template = Liquid::Template.parse("convert {{in_file.tif}} {{out_file.jp2}}")
print @template.render('name' => 'tobi')
print "\n"
@template.root.nodelist.each do |n|
  if n.instance_of?(Liquid::Variable)
    print n.name
    print "\n"
  end
end

# Sinatra style...

# get '/static_file' do
#      send_file('my_static_file')
#   end 


Homebrew Style
==============

require 'formula'

class Foo < Formula
  url 'http://example.com/foo-0.1.tar.gz'
  homepage ''
  sha1 '1234567890ABCDEF1234567890ABCDEF'

  # depends_on 'cmake' => :build

  def install
    system "./configure", "--prefix=#{prefix}", "--disable-debug", "--disable-dependency-tracking"
#   system "cmake", ".", *std_cmake_args
    system "make install"
  end
end



See also: https://github.com/mxcl/homebrew/blob/master/Library/Contributions/example-formula.rb

depends_on 'homebrew/imagemagick' if os.osx?
depends_on 'imagemagick' if os.debian?
depends_on 'imagemagick' if os.linux? # Assumes same package name on all OSs.
