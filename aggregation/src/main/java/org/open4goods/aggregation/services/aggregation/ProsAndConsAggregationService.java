
package org.open4goods.aggregation.services.aggregation;

import java.util.Map;

import org.open4goods.aggregation.AbstractAggregationService;
import org.open4goods.model.data.DataFragment;
import org.open4goods.model.product.AggregatedData;


public class ProsAndConsAggregationService extends AbstractAggregationService {


	public ProsAndConsAggregationService(final String logsFolder) {
		super(logsFolder);
	}

	public @Override void onDataFragment(final DataFragment input, final AggregatedData output) {

		// PROS
		output.getPros().addAll(input.getPros());
		// CONS
		output.getCons().addAll(input.getCons());

	}

}
