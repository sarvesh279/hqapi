package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.DisableMetricResponse;
import org.hyperic.hq.hqapi1.types.EnableMetricResponse;
import org.hyperic.hq.hqapi1.types.GetMetricDataResponse;
import org.hyperic.hq.hqapi1.types.GetMetricResponse;
import org.hyperic.hq.hqapi1.types.GetMetricTemplateResponse;
import org.hyperic.hq.hqapi1.types.Metric;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIndicatorResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIntervalResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultOnResponse;
import org.hyperic.hq.hqapi1.types.SetMetricIntervalResponse;
import org.hyperic.hq.hqapi1.types.ListMetricsResponse;
import org.hyperic.hq.hqapi1.types.GetMetricsDataResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ListMetricTemplatesResponse;

/**
 * The Hyperic HQ Metric API.
 * <br><br>
 * This class provides access to the {@link org.hyperic.hq.hqapi1.types.Metric}s,
 * {@link org.hyperic.hq.hqapi1.types.MetricTemplate}s, and
 * {@link org.hyperic.hq.hqapi1.types.MetricData} within the HQ system.
 * Each of the methods in this class return
 * {@link org.hyperic.hq.hqapi1.types.Response} objects that wrap the result
 * of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class MetricApi extends BaseApi {

    MetricApi(HQConnection conn) {
        super(conn);
    }
    
    /**
     * List all {@link org.hyperic.hq.hqapi1.types.Metric}s associated with a
     * {@link org.hyperic.hq.hqapi1.types.Resource}
     *
     * @param resource The associated {@link org.hyperic.hq.hqapi1.types.Resource} which the metrics belong.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metrics were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ListMetricsResponse listMetrics(Resource resource)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceId", new String[] { Integer.toString(resource.getId()) });
        return doGet("metric/listMetrics.hqu", params,
                     ListMetricsResponse.class);
    }

    /**
     * List all enabled {@link org.hyperic.hq.hqapi1.types.Metric}s associated with a
     * {@link org.hyperic.hq.hqapi1.types.Resource}
     *
     * @param resource The associated {@link org.hyperic.hq.hqapi1.types.Resource}
     * which the metrics belong.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metrics were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ListMetricsResponse listEnabledMetrics(Resource resource)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("resourceId", new String[] { Integer.toString(resource.getId()) });
        params.put("enabled", new String[] { Boolean.toString(true) });
        return doGet("metric/listMetrics.hqu", params,
                     ListMetricsResponse.class);
    }

    /**
     * List all {@link org.hyperic.hq.hqapi1.types.MetricTemplate}s associated
     * with the given {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     *
     * @param prototype The associated {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}
     * to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric templates were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ListMetricTemplatesResponse listMetricTemplates(ResourcePrototype prototype)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("prototype", new String[] { prototype.getName() });
        return doGet("metric/listTemplates.hqu", params,
                     ListMetricTemplatesResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.Metric} associated
     * with the metricId
     *
     * @param id The id used to retrieve the associated
     *  {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.Metric} was retrieved
     * successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetMetricResponse getMetric(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("metric/getMetric.hqu", params,
                     GetMetricResponse.class);
    }
    
    /**
     * Disable a {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @param m The {@link Metric} to disable.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric was successfully disabled.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public DisableMetricResponse disableMetric(Metric m)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(m.getId()) });
        return doGet("metric/disableMetric.hqu", params,
                     DisableMetricResponse.class);
    }

    /**
     * Enable a {@link org.hyperic.hq.hqapi1.types.Metric}.
     *
     * @param m The metric to enable.
     * @param interval The interval for collection in milliseconds.  The
     * interval must be set on 1 minute increments otherwise an invalid
     * arguments error will be returned.
     * 
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric was successfully enabled.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public EnableMetricResponse enableMetric(Metric m, long interval)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(m.getId()) });
        params.put("interval", new String[] { Long.toString(interval) });
        return doGet("metric/enableMetric.hqu", params,
                     EnableMetricResponse.class);
    }

    /**
     * Set a {@link org.hyperic.hq.hqapi1.types.Metric} collection interval.
     *
     * @param m The metric to change the collection interval.
     * @param interval The interval for collection in milliseconds.  The
     * interval must be set on 1 minute increments otherwise an invalid
     * arguments error will be returned.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.Metric}s collection interval was
     * successfully updated.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricIntervalResponse setInterval(Metric m, long interval)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(m.getId()) });
        params.put("interval", new String[] { Long.toString(interval) });
        return doGet("metric/setInterval.hqu", params,
                     SetMetricIntervalResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricTemplate} associated
     * with the metric id.
     *
     * @param id The id used to retrieve the associated
     *  {@link org.hyperic.hq.hqapi1.types.MetricTemplate}.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.MetricTemplate} was retrieved
     * successfully
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetMetricTemplateResponse getMetricTemplate(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("metric/getMetricTemplate.hqu", params,
                     GetMetricTemplateResponse.class);
    }
    
    /**
     * Sets the default on behavior for all
     *  {@link org.hyperic.hq.hqapi1.types.Metric}s associated with this
     *  {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     *
     * @param template The {@link org.hyperic.hq.hqapi1.types.MetricTemplate} to operate on.
     * @param on The flag to set for default on.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * {@link org.hyperic.hq.hqapi1.types.Metric}s collection interval was
     * successfully updated.
     *
     * @see org.hyperic.hq.hqapi1.ErrorCode#INVALID_PARAMETERS
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricDefaultOnResponse setDefaultOn(MetricTemplate template, boolean on)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("templateId", new String[] { Integer.toString(template.getId()) });
        params.put("on", new String[] { Boolean.toString(on) });
        return doGet("metric/setDefaultOn.hqu", params,
                     SetMetricDefaultOnResponse.class);
    }
    
    /**
     * Sets the default indicator for all
     *  {@link org.hyperic.hq.hqapi1.types.Metric}s associated with this
     *  {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     *
     * @param template The {@link org.hyperic.hq.hqapi1.types.MetricTemplate} to operate on.
     * @param on The flag to set for default indicator.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric's collection interval was successfully updated.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricDefaultIndicatorResponse setDefaultIndicator(MetricTemplate template,
                                                                 boolean on)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("templateId", new String[] { Integer.toString(template.getId()) });
        params.put("on", new String[] { Boolean.toString(on) });
        return doGet("metric/setDefaultIndicator.hqu", params,
                     SetMetricDefaultIndicatorResponse.class);
    }
    
    /**
     * Set a {@link org.hyperic.hq.hqapi1.types.Metric} collection interval.
     *
     * @param template The {@link org.hyperic.hq.hqapi1.types.MetricTemplate} to operate on
     * @param interval The interval for collection in milliseconds.  The
     * interval must be set on 1 minute increments otherwise an invalid
     * arguments error will be returned.
     * 
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * metric template's default collection interval was successfully updated.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public SetMetricDefaultIntervalResponse setDefaultInterval(MetricTemplate template,
                                                               long interval)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("templateId", new String[] { Integer.toString(template.getId()) });
        params.put("interval", new String[] { Long.toString(interval) });
        return doGet("metric/setDefaultInterval.hqu", params,
                     SetMetricDefaultIntervalResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the
     * given {@link org.hyperic.hq.hqapi1.types.Metric}
     *
     * @param metricId The id of the {@link org.hyperic.hq.hqapi1.types.Metric} to query.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.GetMetricDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetMetricDataResponse getMetricData(int metricId, long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("metricId", new String[] { Integer.toString(metricId) });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });

        return doGet("metric/getData.hqu", params, GetMetricDataResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the given
     * {@link org.hyperic.hq.hqapi1.types.Group} and {@link org.hyperic.hq.hqapi1.types.MetricTemplate}.
     *
     * @param groupId The id of the compatible {@link org.hyperic.hq.hqapi1.types.Group} to query.
     * @param templateId The id of the {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     * to query for data.  The template must belong to the
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype} for the passed in
     * compatible group.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.GetMetricsDataResponse#getMetricData()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetMetricsDataResponse getMetricData(int groupId, int templateId,
                                                long start, long end)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        params.put("templateId", new String[] { Integer.toString(templateId) });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });

        return doGet("metric/getGroupData.hqu", params,
                     GetMetricsDataResponse.class);
    }

    /**
     * Get the {@link org.hyperic.hq.hqapi1.types.MetricData} for the given
     * list of {@link Resource}s and {@link org.hyperic.hq.hqapi1.types.MetricTemplate}.
     *
     * @param resourceIds The list of {@link org.hyperic.hq.hqapi1.types.Resource}s to
     * query.  It is required that all Resources in this list are of the same
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     * @param templateId The id of the {@link org.hyperic.hq.hqapi1.types.MetricTemplate}
     * to query for data.  The template must belong to the
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype} for the passed in
     * resources.
     * @param start The start time to query, in epoch-millis.
     * @param end The end time to query, in epoch-millis.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS}
     * if the data was succesfully queried.  The returned data can be retrieved
     * via {@link org.hyperic.hq.hqapi1.types.GetMetricsDataResponse#getMetricData()}.
     */
    public GetMetricsDataResponse getMetricData(int[] resourceIds, int templateId,
                                                long start, long end)
        throws IOException {

        Map<String,String[]> params = new HashMap<String,String[]>();
        String[] ids = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            ids[i] = Integer.toString(resourceIds[i]);
        }
        params.put("ids", ids);
        params.put("templateId", new String[] { Integer.toString(templateId) });
        params.put("start", new String[] { Long.toString(start) });
        params.put("end", new String[] { Long.toString(end) });

        return doGet("metric/getResourceData.hqu", params,
                     GetMetricsDataResponse.class);
    }
}
