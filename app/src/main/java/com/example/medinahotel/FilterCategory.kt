package com.example.medinahotel

import android.widget.Filter

class FilterCategory :Filter{

    // in which we want search
    private var filterList: ArrayList<ModelCategory>

    //adapter in which filter need to be implemented
    private var adapterCategory: AdapterCategory

    //constructur
    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) : super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        //value should not be null and empty
        if(constraint != null && constraint.isNotEmpty()){
            //searched value is not null
            //change to upper case or lower case
            constraint = constraint.toString().uppercase()
            val filteredModels: ArrayList<ModelCategory> = ArrayList()

            for(i in 0 until filterList.size){
                if( filterList[i].category.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            // search is null
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>

        //notify changes
        adapterCategory.notifyDataSetChanged()
    }
}