class MonkeyTokenizer

  #attr_reader is ruby short hand for a getter method
  # so in java we'd write getPage() and getToken
  attr_reader :page, :tokens

  #in java this would be the public MonkeyTokenizer(String page){...}
  def initialize(page)
    #puts page
    @current_pos = 0
    @page = page.to_s
    @tokens = Array.new #array of MonkeyTokens
    tokenize
  end

  def get_next_token
    if ?< == @page[@current_pos]   #is this a tag
      if @page[(@current_pos + 1), 3] == "!--" #is this a comment
        #calculate length of token and move token
        tag_token_end = @page.index('-->', @current_pos + 1)
        raise "malformed html" if tag_token_end == nil
        @current_pos += tag.length
      else
        #calculate length of token and move pointer - then add to token list
        tag_token_end = @page.index('>', @current_pos + 1)
        raise "malformed html" if tag_token_end == nil
        tag = @page[@current_pos...tag_token_end + 1]
        @tokens << MonkeyToken.new(tag, :tag)
        @current_pos += tag.length
        
      end
    else
      #calculate length of text and move pointer - then add to token list
      text_token_end = @page.index('<', @current_pos)
      text = @page[@current_pos...text_token_end]
      @tokens << MonkeyToken.new(text, :text)
      @current_pos += text.length
    end
  end

  def tokenize
    while @current_pos < @page.length
      get_next_token
    end
  end

end

class MonkeyToken


  
  attr_reader :full_tag, :tag, :attributes, :type
  
  def initialize(full_tag, type)
    #@variables are class variables

    #<b>
    @full_tag = full_tag

    #:text or :tag
    @type = type

    #:foo => "bar"
    @attributes = Hash.new

    @end_tag = false

    # grab the tag name of the tag, if it's text then just keep the whole thing
    @tag = full_tag if type == :text
    if type == :tag

      #regex to get the a in <a href="b">
      @tag = full_tag.scan(/[\w:-]+/)[0]
      if @tag.nil?
        raise "Error, tag is nil: #{@full_tag}"
      end
      classify_tag
    end
  end

  def has_attributes?
    return !@attributes.empty?
  end

  def is_end_tag?
    return @end_tag
  end

  def is_start_tag?
    return !@end_tag
  end
  
  def classify_tag
    #
    # <a foo="test" bar="test" >
    #   produces three results: 1)a 2.1)foo 2.2)test 3.1)bar 3.2)test
    #
    #   This function creates a hash of the attributes of a tag and determines if this tag is an end tag
    #

    # 'scan' breaks up a string based on a regex, with each group being a new element in an array. Sounds like hassle in java.
    atts = @full_tag.scan(/<[\w:-]+\s+(.*)>/)
    attr_arr = atts[0].to_s.scan(/\s*([\w:-]+)(?:\s*=\s*("[^"]*"|'[^']*'|([^"'>][^\s>]*)))?/m)

    #this is a bit like a for each loop
    attr_arr.each do |n|
      @attributes[n[0].downcase] = n[1]
    end


    #determine if the tag is an end tag by looking for a / before the tag name ( </b> )
    end_tag = @full_tag.index('/', 0)
    if end_tag != nil
      tag_pos = @full_tag.index(@tag, 0)
      @end_tag = true if end_tag < tag_pos
    end

    
  end

end


#little test and useage demo, feel free to butcher.
#we need to decide what to do about really malformed html
page = "<b>test</b><strong class='fakeBold' tes='foo'>bold</strong>"


tokenizer = MonkeyTokenizer.new(page)


tokenizer.tokens.each do |n|
  if n.type == :tag
    string = "start_tag: " if n.is_start_tag?
    string = "end_tag: " if n.is_end_tag?
    string << n.tag

    if n.has_attributes?
      n.attributes.each_pair {|key, value| string << "  with attrubute: #{key} is #{value}" }
    end

    puts string
  end
  
 
  puts "text:" + n.tag if n.type == :text
end



