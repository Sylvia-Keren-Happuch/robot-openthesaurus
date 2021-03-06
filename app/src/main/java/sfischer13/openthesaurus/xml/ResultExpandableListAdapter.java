/*
   Copyright 2015-2017 Stefan Fischer

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package sfischer13.openthesaurus.xml;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sfischer13.openthesaurus.R;
import sfischer13.openthesaurus.model.Synset;
import sfischer13.openthesaurus.model.Term;

public class ResultExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<Group> groups;
    private final LayoutInflater inflater;

    public ResultExpandableListAdapter(Context context, Result result) {
        this.context = context;
        groups = new ArrayList<>();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (result != null) {
            // search suggestions
            if (result.getSuggestions().size() != 0) {
                Group group = new Group(R.string.suggestions);
                for (SuggestionCollection collection : result.getSuggestions()) {
                    String title;
                    switch (collection.getType()) {
                        case SIMILAR:
                            title = context.getString(R.string.suggestion_similar);
                            break;
                        case SUB:
                            title = context.getString(R.string.suggestion_substring);
                            break;
                        case START:
                            title = context.getString(R.string.suggestion_startswith);
                            break;
                        default:
                            title = "";
                    }

                    group.add(new HeaderChild(title));
                    for (Term term : collection.getTerms()) {
                        group.add(new TermChild(term));
                    }
                }
                groups.add(group);
            }

            // search matches
            if (result.getSynsets().size() != 0) {
                Group group = new Group(R.string.matches);
                for (Synset synset : result.getSynsets()) {
                    group.add(new HeaderChild(""));
                    for (Term term : synset.getTerms()) {
                        group.add(new TermChild(term));
                    }
                }
                groups.add(group);
            }
        }
    }

    interface Header {
        String getHeader();
    }

    class Group implements Header {
        private final String header;
        private final List<Child> children;

        public Group(int stringId) {
            header = context.getString(stringId);
            children = new ArrayList<>();
        }

        public void add(Child child) {
            children.add(child);
        }

        @Override
        public String getHeader() {
            return header;
        }
    }

    interface Child {
        boolean isSelectable();
    }

    class HeaderChild implements Child, Header {
        private final String header;

        public HeaderChild(String header) {
            this.header = header;
        }

        @Override
        public String getHeader() {
            return header;
        }

        @Override
        public boolean isSelectable() {
            return false;
        }
    }

    public class TermChild implements Child {
        private final Term term;

        public TermChild(Term term) {
            this.term = term;
        }

        public Term getTerm() {
            return term;
        }

        @Override
        public boolean isSelectable() {
            return true;
        }
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return groups.get(i).children.size();
    }

    @Override
    public Object getGroup(int i) {
        return groups.get(i);
    }

    @Override
    public Object getChild(int i, int j) {
        return groups.get(i).children.get(j);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int j) {
        return j;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.group, parent, false);
        String text = ((Group) getGroup(i)).getHeader();
        ((TextView) view.findViewById(R.id.text)).setText(text);
        return view;
    }

    @Override
    public View getChildView(int i, int j, boolean isLastChild, View convertView, ViewGroup parent) {
        Child child = (Child) getChild(i, j);
        View view;
        if (child instanceof HeaderChild) {
            view = inflater.inflate(R.layout.header, parent, false);
            String text = ((HeaderChild) child).getHeader();
            ((TextView) view.findViewById(R.id.text)).setText(text);
        } else if (child instanceof TermChild) {
            view = inflater.inflate(R.layout.child, parent, false);

            String term = ((TermChild) child).getTerm().getTerm();
            ((TextView) view.findViewById(R.id.text)).setText(term);

            String level = ((TermChild) child).getTerm().getLevelAbbreviation();
            if (level != null) {
                String levelFormatted = String.format("(%s)", level);
                ((TextView) view.findViewById(R.id.level)).setText(levelFormatted);
            }

        } else {
            view = null;
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int j) {
        return groups.get(i).children.get(j).isSelectable();
    }
}