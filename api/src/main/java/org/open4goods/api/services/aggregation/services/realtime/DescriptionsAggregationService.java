package org.open4goods.api.services.aggregation.services.realtime;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.open4goods.api.services.aggregation.AbstractRealTimeAggregationService;
import org.open4goods.config.yml.ui.DescriptionsAggregationConfig;
import org.open4goods.exceptions.AggregationSkipException;
import org.open4goods.model.data.DataFragment;
import org.open4goods.model.data.Description;
import org.open4goods.model.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DescriptionsAggregationService extends AbstractRealTimeAggregationService{

	private static final Logger logger = LoggerFactory.getLogger(DescriptionsAggregationService.class);

	private final DescriptionsAggregationConfig config;

	public DescriptionsAggregationService(final DescriptionsAggregationConfig config,final String logsFolder,boolean toConsole) {
		super(logsFolder,toConsole);
		this.config = config;
	}

	@Override
	public void onDataFragment(DataFragment o, final Product output) {
		// Handle descriptions for a datafragment
		handleDescriptions(o.getDescriptions(), output);
	}

	@Override
	public void handle(Product output) throws AggregationSkipException {
		// Handle descriptions for a product
		handleDescriptions(output.getDescriptions(), output);
	}

	/**
	 * Handle descriptions, including cleaning and truncation
	 * @param descriptions
	 * @param output
	 */
	private void handleDescriptions( final Set<Description> descriptions, Product output) {
		for (final Description d : descriptions) {

			// Strip html tags
			String text = Jsoup.parse(d.getContent().getText()).text();

			// Normalizing spaces
			text = StringUtils.normalizeSpace(text);

			// Cut text

			// NOTE(gof) : Not really nice, should clone. But i know that the descriptions
			// are not used after the "computeDescriptions", that's my archi !
			if (text.length() > config.getDescriptionsTruncationLength()) {
				text = text.substring(0, config.getDescriptionsTruncationLength()) + config.getDescriptionsTruncationSuffix();
				d.setTruncated(true);
			} else {
				d.setTruncated(false);
			}

			d.getContent().setText(text);
			//				d.setProvider
			output.getDescriptions().add(d);
		}
	}


}
