format 224

classcanvas 128002 class_ref 137474 // MainActivity
  classdiagramsettings member_max_width 0 end
  xyz 256 120 2000
end
classcanvas 128130 class_ref 137730 // Movie
  classdiagramsettings member_max_width 0 end
  xyz 192 482 2005
end
classcanvas 128258 class_ref 138114 // ApiClient
  classdiagramsettings member_max_width 0 end
  xyz 608 39 2010
end
classcanvas 128386 class_ref 138242 // MovieResponse
  classdiagramsettings member_max_width 0 end
  xyz 41 527 2011
end
classcanvas 128642 class_ref 138370 // TheMovieDB
  classdiagramsettings member_max_width 0 end
  xyz 384.5 480.5 2000
end
classcanvas 128770 class_ref 138498 // MovieDB
  classdiagramsettings member_max_width 0 end
  xyz 498 54 2000
end
classcanvas 128898 class_ref 137986 // SimpleOnGestureListener
  classdiagramsettings member_max_width 0 end
  xyz 41 240 2000
end
classcanvas 129026 class_ref 137602 // AppCompatActivity
  classdiagramsettings member_max_width 0 end
  xyz 270 40 2006
end
relationcanvas 128514 relation_ref 135554 // <unidirectional association>
  from ref 128386 z 2012 stereotype "<<List>>" xyz 145 568 2012 to ref 128130
  role_a_pos 142 547 3000 no_role_b
  no_multiplicity_a no_multiplicity_b
end
relationcanvas 129154 relation_ref 135298 // <generalisation>
  from ref 128002 z 2007 to ref 129026
  no_role_a no_role_b
  no_multiplicity_a no_multiplicity_b
end
relationcanvas 129538 relation_ref 135938 // <association>
  decenter_begin 483
  decenter_end 389
  from ref 128002 z 2001 to ref 128898
  no_role_a no_role_b
  no_multiplicity_a no_multiplicity_b
end
relationcanvas 129666 relation_ref 136066 // <association>
  from ref 128770 z 2011 to ref 128258
  no_role_a no_role_b
  no_multiplicity_a no_multiplicity_b
end
end
