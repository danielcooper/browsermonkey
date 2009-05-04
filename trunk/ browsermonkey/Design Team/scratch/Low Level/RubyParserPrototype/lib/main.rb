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





class MonkeyParser

  attr_reader :original_page, :document_node


  def initialize(page)
    @original_page = page
    @tokenizer = MonkeyTokenizer.new page
    @tokens = @tokenizer.tokens


    #define all the tags and their general attributes. In the actual implementation
    #it might be useful to derive this from a properties file

    @single_nestable_tags = ['html','head','body'] #tags that can only be used once
    @table_tags = ['table','tr','td'] #table tags need a special case
    @nestable_tags = ['b','i','strong','em'] #normal, nestable, tags
    @singuarly_nestable_tags = ['p'] #these tags cannot be nested inside themselfs
    @leaf_tags =['br','img'] #can have no children
    @listed_tags =['li','ol','ul'] #list elements also need a special case
    @open_elements = Array.new


  end

  def parse

    @tokens.each_with_index do |token,i|
      if i == 0 && token.tag != "html"
        @document_node = MonkeyDocumentNode.new("html", :tag)
        @open_elements << @document_node
      end
      if token.type == :tag
        if token.is_start_tag?
          if @table_tags.member? token.tag
            do_table_element token
          elsif @open_elements.length >= 1
            if @open_elements.last.tag == "tr" || @open_elements.last.tag == "table"
              do_table_element MonkeyToken.new '<td>', :tag
            end
          end

          if @listed_tags.member? token.tag
            do_listed_element token
          end

          #pre elements should not have any children, just text
           if @open_elements.length >= 1 && @open_elements.last.tag == "pre"
               @open_elements.last.text = @open_elements.last.text + token.full_tag
           end

          if @singuarly_nestable_tags.member? token.tag
            if @open_elements.length > 1 && @open_elements.last == token.tag
              do_end_token token
              do_start_token token
            end
          end

          if @nestable_tags.member? token.tag
            do_start_token token
          end

          if @leaf_tags.member? token.tag
            do_leaf_element token
          end
        else
          do_end_token token
        end
      else
        if @open_elements.length >= 1
            if @open_elements.last.tag == "tr" || @open_elements.last.tag == "table"
              do_table_element MonkeyToken.new '<td>', :tag
            end
          end
        do_text_element token
      end

    end
  end


    def do_listed_element token
      if token.tag == 'li'
        if @open_elements.length >= 1
          if @open_elements[-1].tag == "ol" || @open_elements[-1].tag == "ul"
            do_start_token token
          elsif @open_elements[-1].tag == "li"
            do_end_token @open_elements[-1]
            do_start_token token
          else
            do_listed_element MonkeyToken.new '<ul>', :tag
            do_start_token token
          end
        else
          do_table_token MonkeyToken.new '<ul>', :tag
          do_start_token token
        end

      elsif token.tag == 'ol' || token.tag == 'ul'
        do_start_token token
      end

    end

    def do_table_element token
      if token.tag == 'td'
        if @open_elements.length >= 1
          if @open_elements[-1].tag == "tr"
            do_start_token token
          else
            do_table_element MonkeyToken.new '<tr>', :tag
            do_start_token token
          end
        else
          do_table_token MonkeyToken.new '<tr>', :tag
          do_start_token token
        end
      
      elsif token.tag == 'tr'
        if @open_elements.length >= 1
          if @open_elements[-1].tag == "table"
            do_start_token token
          else
            do_table_element MonkeyToken.new '<table>', :tag
            do_start_token token
          end
        else
          do_table_element MonkeyToken.new '<table>', :tag
          do_start_token token
        end
    
      elsif token.tag == "table"
        do_start_token token
      end
    end

    def do_leaf_element token
      @open_elements.last << MonkeyDocumentNode.new(token.tag, token.type, token.attributes)
    end

    def do_text_element token
      do_leaf_element token
    end

    def fix_nesting_error token
      @open_elements.reverse_each do |element|
        if element.tag == token.tag
          @open_elements.delete element
          break
        end
      end
    end
  
    def do_end_token token
      if token.tag == @open_elements.last.tag
        @open_elements.pop
      else
        fix_nesting_error token
      end
    end



    def do_start_token token
      new_node = MonkeyDocumentNode.new(token.tag, token.type, token.attributes)

      @open_elements.last << new_node
      @open_elements << new_node
      @document_node << @open_elements.first if @open_elements.size == 1
    end

  end

  class MonkeyDocumentNode
    #A MonkeyDocumentNode is a node in a tree. It has children and a parent.


    #both a getter and a setter
    attr_accessor :type, :tag, :attributes, :children

    def initialize(value, type, attributes = Hash.new)
  
      @tag = value
      @type = type
      @children = Array.new
      @attributes = attributes
    end


    def text
      return @tag
    end

    def text=(text)
      @tag = text
    end


    #This allow to add documentnode to the children of a parent node  documentnode1 << documentnode2
    def <<(value)
      @children << value
      return value
    end

    #each provides the standard ruby way of itterating over a collection
    #We're traversing the tree recurivly and each time we find an element we yield back to the calling block.
    def each
      yield value
      @children.each do |child_node|
        child_node.each { |e| yield e }
      end
    end

    #same as overiding the toString() method in java
    def to_s

    end

  end







  #little test and useage demo, feel free to butcher.
  #we need to decide what to do about really malformed html
  page = "<ul><b>test</b><li>lol</li></ul>"
  #"<b>test<strong class='fakeBold' tes='foo'>bold</strong></b>"


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


  parser = MonkeyParser.new(page)
  parser.parse
  bar = "foo"


