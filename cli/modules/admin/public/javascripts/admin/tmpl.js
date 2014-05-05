(function(/*! Brunch !*/) {
  'use strict';

  var globals = typeof window !== 'undefined' ? window : global;
  if (typeof globals.require === 'function') return;

  var modules = {};
  var cache = {};

  var has = function(object, name) {
    return ({}).hasOwnProperty.call(object, name);
  };

  var expand = function(root, name) {
    var results = [], parts, part;
    if (/^\.\.?(\/|$)/.test(name)) {
      parts = [root, name].join('/').split('/');
    } else {
      parts = name.split('/');
    }
    for (var i = 0, length = parts.length; i < length; i++) {
      part = parts[i];
      if (part === '..') {
        results.pop();
      } else if (part !== '.' && part !== '') {
        results.push(part);
      }
    }
    return results.join('/');
  };

  var dirname = function(path) {
    return path.split('/').slice(0, -1).join('/');
  };

  var localRequire = function(path) {
    return function(name) {
      var dir = dirname(path);
      var absolute = expand(dir, name);
      return globals.require(absolute, path);
    };
  };

  var initModule = function(name, definition) {
    var module = {id: name, exports: {}};
    cache[name] = module;
    definition(module.exports, localRequire(name), module);
    return module.exports;
  };

  var require = function(name, loaderPath) {
    var path = expand(name, '.');
    if (loaderPath == null) loaderPath = '/';

    if (has(cache, path)) return cache[path].exports;
    if (has(modules, path)) return initModule(path, modules[path]);

    var dirIndex = expand(path, './index');
    if (has(cache, dirIndex)) return cache[dirIndex].exports;
    if (has(modules, dirIndex)) return initModule(dirIndex, modules[dirIndex]);

    throw new Error('Cannot find module "' + name + '" from '+ '"' + loaderPath + '"');
  };

  var define = function(bundle, fn) {
    if (typeof bundle === 'object') {
      for (var key in bundle) {
        if (has(bundle, key)) {
          modules[key] = bundle[key];
        }
      }
    } else {
      modules[bundle] = fn;
    }
  };

  var list = function() {
    var result = [];
    for (var item in modules) {
      if (has(modules, item)) {
        result.push(item);
      }
    }
    return result;
  };

  globals.require = require;
  globals.require.define = define;
  globals.require.register = define;
  globals.require.list = list;
  globals.require.brunch = true;
})();
require.register("js/views/admin/menu/new", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"btn-group\">\n  <button type=\"button\" class=\"new-user btn btn-md btn-primary\"><span>New User</span></button>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/menu/tag", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;
var locals_ = (locals || {}),color = locals_.color,href = locals_.href,icon = locals_.icon,title = locals_.title;
var jade_indent = [];
buf.push("\n<div" + (jade.attr("style", "background-color:" + (color||'#fff') + "", true, false)) + " class=\"color\">&nbsp;</div><a" + (jade.attr("href", href, true, false)) + " class=\"xlabel\">\n  <div" + (jade.cls(['glyphicon',icon], [null,true])) + "><span class=\"title\">" + (jade.escape((jade_interp = title) == null ? '' : jade_interp)) + "</span></div></a>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/trash/contextmenu", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<ul role=\"menu\" class=\"dropdown-menu squared fixed\">\n  <li><a href=\"#\" class=\"purge\"><span class=\"glyphicon glyphicon-exclamation-sign\"></span><span>Delete Forever  </span></a></li>\n  <li><a href=\"#\" class=\"restore\"><span class=\"glyphicon glyphicon-save\"></span><span>Restore</span></a></li>\n</ul>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/trash/list", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;
var locals_ = (locals || {}),perPage = locals_.perPage;
var jade_indent = [];
buf.push("\n<div class=\"selections\">\n  <div class=\"empty\"><span tabindex=\"0\" role=\"link\" class=\"empty-all\">Empty Trash</span><span></span>&nbsp;(Users in Trash will automatically be deleted in 30 days.)</div>\n  <div class=\"all\"><span>All users are selected. &nbsp;</span><span tabindex=\"0\" role=\"link\" class=\"clear\">Clear selection  </span></div>\n  <div class=\"page\"><span>All<b>" + (jade.escape(null == (jade_interp = perPage) ? "" : jade_interp)) + "</b>users on this page are selected. &nbsp;</span><span tabindex=\"0\" role=\"link\" class=\"select-all\">Select all users in Trash</span></div>\n</div>\n<div class=\"shadow\"></div>\n<div class=\"scrollable\">\n  <div class=\"list\"></div>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/trash/list.toolbar", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"toolbar btn-toolbar empty\">\n  <div id=\"users-trash-select\" class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Select\" type=\"button\" class=\"select btn btn-default btn-md\">\n      <div class=\"checker\"></div>\n      <div type=\"button\" data-toggle=\"dropdown\" data-target=\"#users-trash-select\" class=\"select-toggle-wrapper select-toggle\"><span class=\"caret\"></span></div>\n    </button>\n    <ul role=\"menu\" class=\"selection-menu dropdown-menu squared\">\n      <li><a href=\"#\" data-state=\"select:all\">All</a></li>\n      <li><a href=\"#\" data-state=\"select:none\">None</a></li>\n      <li><a href=\"#\" data-state=\"select:active\">Active</a></li>\n      <li><a href=\"#\" data-state=\"select:suspended\">Suspended</a></li>\n    </ul>\n  </div>\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Refresh\" type=\"button\" class=\"refresh btn btn-default btn-md\"><span class=\"glyphicon glyphicon-refresh\"></span></button>\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Delete Forever\" type=\"button\" class=\"selection purge btn btn-default btn-md\"><span class=\"glyphicon glyphicon-exclamation-sign\"></span></button>\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Restore\" type=\"button\" class=\"selection restore btn btn-default btn-md\"><span class=\"glyphicon glyphicon-upload\"></span></button>\n  </div>\n  <div class=\"paging\"><span class=\"paging-info\"><strong class=\"interval\"><span class=\"start\"></span>–<span class=\"end\"></span></strong>of <strong class=\"count\"></strong></span>\n    <div class=\"btn-group\">\n      <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Previous\" type=\"button\" class=\"prev btn btn-default btn-md\"><span class=\"glyphicon glyphicon-chevron-left\"></span></button>\n      <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Next\" type=\"button\" class=\"next btn btn-default btn-md\"><span class=\"glyphicon glyphicon-chevron-right\"></span></button>\n    </div>\n  </div>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/trash/row", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"user tr\"> \n  <div class=\"td select\">\n    <div class=\"checker\"></div>\n  </div>\n  <div class=\"td purge\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Delete Forever\" type=\"button\" class=\"selection purge btn btn-link btn-md\"><span class=\"glyphicon glyphicon-exclamation-sign\">   </span></button>\n  </div>\n  <div class=\"td\"><span><b class=\"fullName\"></b></span></div>\n  <div class=\"td\"><span><b class=\"primaryEmail\"></b></span></div>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/trash/single", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"user\"></div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/trash/single.toolbar", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"toolbar btn-toolbar\">\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Back\" type=\"button\" class=\"btn back btn-md btn-primary\"><span class=\"glyphicon glyphicon-chevron-left\"></span><span>&nbsp; Back to Trash</span></button>\n  </div>\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Refresh\" type=\"button\" class=\"refresh btn btn-default btn-md\"><span class=\"glyphicon glyphicon-refresh\"></span></button>\n  </div>\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Delete Forever\" type=\"button\" class=\"purge btn btn-default btn-md\"><span class=\"glyphicon glyphicon-exclamation-sign\"></span></button>\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Restore\" type=\"button\" class=\"restore btn btn-default btn-md\"><span class=\"glyphicon glyphicon-save\"></span></button>\n  </div>\n</div>\n<div class=\"shadow\"></div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/contextmenu", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<ul role=\"menu\" class=\"dropdown-menu squared fixed\">\n  <li><a href=\"#\" class=\"edit\"><span class=\"glyphicon glyphicon-pencil\"></span><span>Edit</span></a></li>\n  <li><a href=\"#\" class=\"purge\"><span class=\"glyphicon glyphicon-trash\"></span><span>Delete</span></a></li>\n  <li><a href=\"#\" class=\"purge\"><span class=\"glyphicon glyphicon-lock\"></span><span>Suspend</span></a></li>\n  <li class=\"divider\"></li>\n  <li><a href=\"#\" class=\"purge\"><span class=\"glyphicon glyphicon-edit\"></span><span>Change Password</span></a></li>\n  <li><a href=\"#\" class=\"purge\"><span class=\"glyphicon glyphicon-envelope\"></span><span>Send Invitation</span></a></li>\n</ul>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/form.edit", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<ul id=\"profiles\" class=\"nav nav-tabs\">\n  <li class=\"active\"><a data-toggle=\"tab\" data-target=\"#basic_edit\" href=\"#\">Basic</a></li>\n  <li><a data-toggle=\"tab\" data-target=\"#addresses_edit\" href=\"#\">Addresses</a></li>\n  <li><a data-toggle=\"tab\" data-target=\"#contacts_edit\" href=\"#\">Contacts</a></li>\n  <li><a data-toggle=\"tab\" data-target=\"#access_rights_edit\" href=\"#\">Access Rights    </a></li>\n</ul>\n<fieldset>\n  <div id=\"profiles_content\" class=\"tab-content\">\n    <div id=\"basic_edit\" class=\"tab-pane fade in active\">\n      <div class=\"row\">\n        <div class=\"col-xs-6 col-md-3 avatar_fm_wrapper\">\n          <div class=\"avatar_fm\"><a class=\"edit\">Change Picture</a><a class=\"purge\">Delete Picture</a></div><img id=\"avatar\" src=\"http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm&amp;f=y&amp;s=195\" style=\"height: 180px; width: 100%; display: block; max-width: 195px;\" title=\"Click to change your profile picture\" class=\"img-thumbnail\"/>\n        </div>\n        <div class=\"col-xs-12 col-md-6\">\n          <div id=\"actif\">\n            <label>Active (&nbsp;\n              <input type=\"checkbox\" name=\"active\" checked=\"checked\"/>&nbsp;)          \n            </label>\n          </div>\n          <div id=\"name\">\n            <div class=\"form-group\">                   \n              <input type=\"text\" name=\"givenName\" placeholder=\"First name\" required=\"required\" autofocus=\"autofocus\" class=\"form-control\"/>\n            </div>\n            <div class=\"form-group\">                   \n              <input type=\"text\" name=\"familyName\" placeholder=\"Last name\" required=\"required\" class=\"form-control\"/>\n            </div>\n          </div>\n          <div class=\"form-group\">\n            <input type=\"email\" name=\"primaryEmail\" placeholder=\"Enter email\" required=\"required\" class=\"form-control\"/>\n          </div>\n          <div id=\"gender\" class=\"form-group\">\n            <label class=\"radio-inline\">\n              <input type=\"radio\" name=\"gender\" value=\"Male\" checked=\"checked\"/>Male\n            </label>\n            <label class=\"radio-inline\">\n              <input type=\"radio\" name=\"gender\" value=\"Female\"/>Female                                    \n            </label>\n          </div>\n        </div>\n      </div>\n    </div>\n    <div id=\"addresses_edit\" class=\"tab-pane fade\">\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Work</h4>\n        </div>\n        <div class=\"form-group\">\n          <select id=\"work-country\" name=\"workAddress[country]\" data-placeholder=\"Your work country\" placeholder=\"Select country\" autofocus=\"autofocus\" class=\"form-control\"></select>\n        </div>\n        <div class=\"form-group\">\n          <input id=\"work-city\" type=\"text\" name=\"workAddress[city]\" placeholder=\"City\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"workAddress[postalCode]\" placeholder=\"Postal Code\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <textarea name=\"workAddress[streetAddress]\" placeholder=\"Address\" class=\"form-control\"></textarea>\n        </div>\n      </div>\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Home</h4>\n        </div>\n        <div class=\"form-group\">\n          <select id=\"home-country\" name=\"homeAddress[country]\" data-placeholder=\"Your home country\" placeholder=\"Select country\" class=\"form-control\"></select>\n        </div>\n        <div class=\"form-group\">\n          <input id=\"home-city\" type=\"text\" name=\"homeAddress[city]\" placeholder=\"City\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"homeAddress[postalCode]\" placeholder=\"Postal Code\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <textarea name=\"homeAddress[streetAddress]\" placeholder=\"Address\" class=\"form-control\"></textarea>\n        </div>\n      </div>\n    </div>\n    <div id=\"contacts_edit\" class=\"tab-pane fade\">\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Mobile</h4>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[mobiles][mobile1]\" placeholder=\"Enter mobile number 1\" autofocus=\"autofocus\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[mobiles][mobile2]\" placeholder=\"Enter mobile number 2\" class=\"form-control\"/>\n        </div>\n      </div>\n      <div class=\"container\">   \n        <div class=\"page-header\">\n          <h4>Work</h4>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[work][phoneNumber]\" placeholder=\"Enter phone number\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[work][fax]\" placeholder=\"Fax\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"email\" name=\"contacts[work][email]\" placeholder=\"Email\" class=\"form-control\"/>\n        </div>\n      </div>\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Home</h4>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[home][phoneNumber]\" placeholder=\"Enter phone number\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[home][fax]\" placeholder=\"Fax\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"email\" name=\"contacts[home][email]\" placeholder=\"Email\" class=\"form-control\"/>\n        </div>\n      </div>\n    </div>\n    <div id=\"access_rights_edit\" class=\"tab-pane fade\">\n      <div class=\"container\"><br/>\n        <div class=\"modules\">\n          <div class=\"form-group\">\n            <label>Admin</label>\n            <select class=\"form-control\">\n              <option>Admin</option>\n              <option>Admin - Settings</option>\n              <option>Admin - Read only</option>\n            </select>\n          </div>\n          <div class=\"form-group\">\n            <label>Module 1</label>\n            <select class=\"form-control\">\n              <option>Right 1</option>\n            </select>\n          </div>\n          <div class=\"form-group\">\n            <label>Module 2</label>\n            <select class=\"form-control\">\n              <option>Right 1            </option>\n            </select>\n          </div>\n        </div>\n      </div>\n    </div>\n  </div>\n</fieldset>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/form.new", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<ul id=\"profiles\" class=\"nav nav-tabs\">\n  <li class=\"active\"><a data-toggle=\"tab\" data-target=\"#basic_new\" href=\"#\">Basic</a></li>\n  <li><a data-toggle=\"tab\" data-target=\"#addresses_new\" href=\"#\">Addresses</a></li>\n  <li><a data-toggle=\"tab\" data-target=\"#contacts_new\" href=\"#\">Contacts</a></li>\n  <li><a data-toggle=\"tab\" data-target=\"#access_rights_new\" href=\"#\">Access Rights</a></li>\n</ul>\n<fieldset>\n  <div id=\"profiles_content\" class=\"tab-content\">\n    <div id=\"basic_new\" class=\"tab-pane fade in active\">\n      <div class=\"row\">\n        <div class=\"col-xs-6 col-md-3 avatar_fm_wrapper\">\n          <div class=\"avatar_fm\"><a class=\"edit\">Change Picture</a><a class=\"purge\">Delete Picture</a></div><img id=\"avatar\" src=\"http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm&amp;f=y&amp;s=195\" style=\"height: 180px; width: 100%; display: block; max-width: 195px;\" title=\"Click to change your profile picture\" class=\"img-thumbnail\"/>\n        </div>\n        <div class=\"col-xs-12 col-md-6\">\n          <div id=\"actif\">\n            <label>Active (&nbsp;\n              <input type=\"checkbox\" name=\"active\" checked=\"checked\"/>&nbsp;)\n            </label>\n          </div>\n          <div id=\"name\">\n            <div class=\"form-group\">                   \n              <input type=\"text\" name=\"givenName\" placeholder=\"First name\" required=\"required\" autofocus=\"autofocus\" class=\"form-control\"/>\n            </div>\n            <div class=\"form-group\">                   \n              <input type=\"text\" name=\"familyName\" placeholder=\"Last name\" required=\"required\" class=\"form-control\"/>\n            </div>\n          </div>\n          <div class=\"form-group\">\n            <input type=\"email\" name=\"primaryEmail\" placeholder=\"Enter email\" required=\"required\" class=\"form-control\"/>\n          </div>\n          <div id=\"gender\" class=\"form-group\">\n            <label class=\"radio-inline\">\n              <input type=\"radio\" name=\"gender\" value=\"Male\" checked=\"checked\"/>Male\n            </label>\n            <label class=\"radio-inline\">\n              <input type=\"radio\" name=\"gender\" value=\"Female\"/>Female                                    \n            </label>\n          </div>\n        </div>\n      </div>\n    </div>\n    <div id=\"addresses_new\" class=\"tab-pane fade\">\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Work</h4>\n        </div>\n        <div class=\"form-group\">\n          <select id=\"work-country\" name=\"workAddress[country]\" data-placeholder=\"Your work country\" placeholder=\"Select country\" autofocus=\"autofocus\" class=\"form-control\"></select>\n        </div>\n        <div class=\"form-group\">\n          <input id=\"work-city\" type=\"text\" name=\"workAddress[city]\" placeholder=\"City\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"workAddress[postalCode]\" placeholder=\"Postal Code\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <textarea name=\"workAddress[streetAddress]\" placeholder=\"Address\" class=\"form-control\"></textarea>\n        </div>\n      </div>\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Home</h4>\n        </div>\n        <div class=\"form-group\">\n          <select id=\"home-country\" name=\"homeAddress[country]\" data-placeholder=\"Your home country\" placeholder=\"Select country\" class=\"form-control\"></select>\n        </div>\n        <div class=\"form-group\">\n          <input id=\"home-city\" type=\"text\" name=\"homeAddress[city]\" placeholder=\"City\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"homeAddress[postalCode]\" placeholder=\"Postal Code\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <textarea name=\"homeAddress[streetAddress]\" placeholder=\"Address\" class=\"form-control\"></textarea>\n        </div>\n      </div>\n    </div>\n    <div id=\"contacts_new\" class=\"tab-pane fade\">\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Mobile</h4>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[mobiles][mobile1]\" placeholder=\"Enter mobile number 1\" autofocus=\"autofocus\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[mobiles][mobile2]\" placeholder=\"Enter mobile number 2\" class=\"form-control\"/>\n        </div>\n      </div>\n      <div class=\"container\">   \n        <div class=\"page-header\">\n          <h4>Work</h4>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[work][phoneNumber]\" placeholder=\"Enter phone number\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[work][fax]\" placeholder=\"Fax\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"email\" name=\"contacts[work][email]\" placeholder=\"Email\" class=\"form-control\"/>\n        </div>\n      </div>\n      <div class=\"container\">\n        <div class=\"page-header\">\n          <h4>Home</h4>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[home][phoneNumber]\" placeholder=\"Enter phone number\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"text\" name=\"contacts[home][fax]\" placeholder=\"Fax\" class=\"form-control\"/>\n        </div>\n        <div class=\"form-group\">\n          <input type=\"email\" name=\"contacts[home][email]\" placeholder=\"Email\" class=\"form-control\"/>\n        </div>\n      </div>\n    </div>\n    <div id=\"access_rights_new\" class=\"tab-pane fade\">\n      <div class=\"container\"><br/>\n        <div class=\"modules\">\n          <div class=\"form-group\">\n            <label>Admin</label>\n            <select class=\"form-control\">\n              <option>Admin</option>\n              <option>Admin - Settings</option>\n              <option>Admin - Read only</option>\n            </select>\n          </div>\n          <div class=\"form-group\">\n            <label>Module 1</label>\n            <select class=\"form-control\">\n              <option>Right 1</option>\n            </select>\n          </div>\n          <div class=\"form-group\">\n            <label>Module 2</label>\n            <select class=\"form-control\">\n              <option>Right 1</option>\n            </select>\n          </div>\n        </div>\n      </div>\n    </div>\n  </div>\n</fieldset>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/form.toolbar", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;
var locals_ = (locals || {}),title = locals_.title;
var jade_indent = [];
buf.push("\n<div class=\"toolbar btn-toolbar\">\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Back\" type=\"button\" class=\"btn back btn-md btn-link\"><span class=\"glyphicon glyphicon-chevron-left\"></span><span></span></button>\n  </div>\n  <div class=\"btn-group title\">\n    <h3 class=\"title\">" + (jade.escape(null == (jade_interp = title) ? "" : jade_interp)) + "</h3>\n  </div>\n  <div class=\"btn-group pull-right\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" data-saving-text=\"Saving...\" title=\"Save\" type=\"button\" class=\"save btn btn-primary btn-md\"><span class=\"spin\"></span><span>Save</span></button>\n  </div>\n</div>\n<div class=\"shadow\"></div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/label", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;
var locals_ = (locals || {}),name = locals_.name;
var jade_indent = [];
buf.push("\n<div class=\"label tr\"> \n  <div class=\"td select\">\n    <div class=\"checker\"></div>\n  </div>\n  <div class=\"td\"><span><b>" + (null == (jade_interp = name) ? "" : jade_interp) + "</b></span></div>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/list", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;
var locals_ = (locals || {}),perPage = locals_.perPage;
var jade_indent = [];
buf.push("\n<div class=\"selections\">\n  <div class=\"all\"><span>All users are selected. &nbsp;</span><span tabindex=\"0\" role=\"link\" class=\"clear\">Clear selection  </span></div>\n  <div class=\"page\"><span>All<b>" + (jade.escape(null == (jade_interp = perPage) ? "" : jade_interp)) + "</b>users on this page are selected. &nbsp;</span><span tabindex=\"0\" role=\"link\" data-state=\"select:all\" class=\"select-all\">Select all users</span></div>\n</div>\n<div class=\"shadow\"></div>\n<div class=\"scrollable\">\n  <div class=\"list\"></div>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/list.toolbar", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"toolbar btn-toolbar empty\">\n  <div id=\"users-select\" class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Select\" type=\"button\" class=\"select btn btn-default btn-md\">\n      <div class=\"checker\"></div>\n      <div type=\"button\" data-toggle=\"dropdown\" data-target=\"#users-select\" class=\"select-toggle-wrapper select-toggle\"><span class=\"caret\"></span></div>\n    </button>\n    <ul role=\"menu\" class=\"selection-menu dropdown-menu squared\">\n      <li><a href=\"#\" data-state=\"select:all\">All</a></li>\n      <li><a href=\"#\" data-state=\"select:none\">None</a></li>\n      <li><a href=\"#\" data-state=\"select:active\">Active</a></li>\n      <li><a href=\"#\" data-state=\"select:suspended\">Suspended</a></li>\n    </ul>\n  </div>\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Refresh\" type=\"button\" class=\"refresh btn btn-default btn-md\"><span class=\"glyphicon glyphicon-refresh\"></span></button>\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Delete\" type=\"button\" class=\"selection purge btn btn-default btn-md\"><span class=\"glyphicon glyphicon-trash\"></span></button>\n  </div>\n  <div class=\"labels-menu btn-group\">\n    <button type=\"button\" data-toggle=\"dropdown\" class=\"selection btn btn-default btn-md dropdown-toggle\"><span class=\"glyphicon glyphicon-tags\"></span>&nbsp;&nbsp;Labels &nbsp;<span class=\"caret\"></span></button>\n    <ul class=\"dropdown-menu squared dropdown-menu-left\">\n      <li>\n        <div class=\"labels-list-wrapper\">\n          <form class=\"tags\">\n            <fieldset>\n              <div class=\"filter\">\n                <input type=\"text\" placeholder=\"Search\" autofocus=\"autofocus\" class=\"form-control squared input-sm\"/><span class=\"glyphicon glyphicon-search\"></span>\n              </div>\n              <div class=\"shadow\"></div>\n              <div class=\"scrollable\">\n                <div class=\"labels-list\"></div>\n              </div>\n            </fieldset>\n          </form>\n        </div>\n      </li>\n      <li class=\"divider\"></li>\n      <li class=\"newLabel\"><a href=\"#\" class=\"new-label\">New Label    </a></li>\n      <li class=\"manage\"><a href=\"#\" class=\"manage-labels\">Manage Labels    </a></li>\n      <li class=\"apply\"><a href=\"#\" class=\"apply-labels\">Apply    </a></li>\n    </ul>\n  </div>\n  <div class=\"more btn-group\">\n    <button type=\"button\" data-toggle=\"dropdown\" class=\"btn btn-default btn-md dropdown-toggle\">More &nbsp;<span class=\"caret\"></span></button>\n    <ul class=\"dropdown-menu squared\">\n      <li><a href=\"#\">Import</a></li>\n      <li><a href=\"#\">Export</a></li>\n      <li class=\"divider\"></li>\n      <li><a href=\"#\">Disable</a></li>\n    </ul>\n  </div>\n  <div class=\"paging\"><span class=\"paging-info\"><strong class=\"interval\"><span class=\"start\"></span>–<span class=\"end\"></span></strong>of <strong class=\"count\"></strong></span>\n    <div class=\"btn-group\">\n      <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Previous\" type=\"button\" class=\"prev btn btn-default btn-md\"><span class=\"glyphicon glyphicon-chevron-left\"></span></button>\n      <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Next\" type=\"button\" class=\"next btn btn-default btn-md\"><span class=\"glyphicon glyphicon-chevron-right\"></span></button>\n    </div>\n  </div>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/row", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"user tr\"> \n  <div class=\"td select\">\n    <div class=\"checker\"></div>\n  </div>\n  <div class=\"td\"><span><b class=\"fullName\"></b></span></div>\n  <div class=\"td\"><span><b class=\"primaryEmail\"></b></span></div>\n</div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/single", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"user\"></div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;require.register("js/views/admin/user/single.toolbar", function(exports, require, module) {
var __templateData = function template(locals) {
var buf = [];
var jade_mixins = {};
var jade_interp;

var jade_indent = [];
buf.push("\n<div class=\"toolbar btn-toolbar\">\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Back\" type=\"button\" class=\"btn back btn-md btn-primary\"><span class=\"glyphicon glyphicon-chevron-left\"></span><span>&nbsp; Back</span></button>\n  </div>\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Refresh\" type=\"button\" class=\"refresh btn btn-default btn-md\"><span class=\"glyphicon glyphicon-refresh\"></span></button>\n  </div>\n  <div class=\"btn-group\">\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Edit\" type=\"button\" class=\"edit btn btn-default btn-md\"><span class=\"glyphicon glyphicon-pencil\"></span></button>\n    <button data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Delete\" type=\"button\" class=\"purge btn btn-default btn-md\"><span class=\"glyphicon glyphicon-trash\"></span></button>\n  </div>\n  <div class=\"more btn-group\">\n    <button type=\"button\" data-toggle=\"dropdown\" class=\"btn btn-default btn-md dropdown-toggle\">More &nbsp;<span class=\"caret\"></span></button>\n    <ul class=\"dropdown-menu squared\">\n      <li><a href=\"#\">Import</a></li>\n      <li><a href=\"#\">Export</a></li>\n      <li class=\"divider\"></li>\n      <li><a href=\"#\">Disable</a></li>\n    </ul>\n  </div>\n</div>\n<div class=\"shadow\"></div>");;return buf.join("");
};
if (typeof define === 'function' && define.amd) {
  define([], function() {
    return __templateData;
  });
} else if (typeof module === 'object' && module && module.exports) {
  module.exports = __templateData;
} else {
  __templateData;
}
});

;
//# sourceMappingURL=tmpl.js.map