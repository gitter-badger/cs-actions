package io.cloudslang.content.vmware.actions.cluster;

import com.hp.oo.sdk.content.annotations.Action;
import com.hp.oo.sdk.content.annotations.Output;
import com.hp.oo.sdk.content.annotations.Param;
import com.hp.oo.sdk.content.annotations.Response;
import com.hp.oo.sdk.content.plugin.ActionMetadata.MatchType;
import com.hp.oo.sdk.content.plugin.ActionMetadata.ResponseType;
import io.cloudslang.content.vmware.constants.Inputs;
import io.cloudslang.content.vmware.constants.Outputs;
import io.cloudslang.content.vmware.entities.VmInputs;
import io.cloudslang.content.vmware.entities.http.HttpInputs;
import io.cloudslang.content.vmware.services.ClusterComputeResourceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.cloudslang.content.vmware.constants.VmRestartPriorities.CLUSTER_RESTART_PRIORITY;
import static io.cloudslang.content.vmware.constants.VmRestartPriorities.DISABLED;
import static io.cloudslang.content.vmware.constants.VmRestartPriorities.HIGH;
import static io.cloudslang.content.vmware.constants.VmRestartPriorities.LOW;
import static io.cloudslang.content.vmware.constants.VmRestartPriorities.MEDIUM;

/**
 * Created by giloan on 9/1/2016.
 */
public class ModifyVmOverrides {

    private static final String SEPARATOR = ",";
    private static final String INVALID_RESTART_PRIORITY_MSG = "The 'restartPriority' input value is not valid! Valid values are ";

    @Action(name = "Modify VM Overrides Priorities",
            outputs = {
                    @Output(Outputs.RETURN_CODE),
                    @Output(Outputs.RETURN_RESULT),
                    @Output(Outputs.EXCEPTION)
            },
            responses = {
                    @Response(text = Outputs.SUCCESS, field = Outputs.RETURN_CODE, value = Outputs.RETURN_CODE_SUCCESS,
                            matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.RESOLVED),
                    @Response(text = Outputs.FAILURE, field = Outputs.RETURN_CODE, value = Outputs.RETURN_CODE_FAILURE,
                            matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.ERROR, isOnFail = true)
            })
    public Map<String, String> modifyVmOverrides(@Param(value = Inputs.HOST, required = true) String host,
                                       @Param(value = Inputs.PORT) String port,
                                       @Param(value = Inputs.PROTOCOL) String protocol,
                                       @Param(value = Inputs.USERNAME, required = true) String username,
                                       @Param(value = Inputs.PASSWORD, encrypted = true) String password,
                                       @Param(value = Inputs.TRUST_EVERYONE) String trustEveryone,
                                       @Param(value = Inputs.HOSTNAME, required = true) String hostname,
                                       @Param(value = Inputs.VM_NAME, required = true) String virtualMachineName,
                                       @Param(value = Inputs.CLUSTER_NAME, required = true) String clusterName,
                                       @Param(value = Inputs.RESTART_PRIORITY, required = true) String restartPriority) {

        Map<String, String> resultMap;
        try {
            HttpInputs httpInputs = new HttpInputs.HttpInputsBuilder()
                    .withHost(host)
                    .withPort(port)
                    .withProtocol(protocol)
                    .withUsername(username)
                    .withPassword(password)
                    .withTrustEveryone(trustEveryone)
                    .build();

            VmInputs vmInputs = new VmInputs.VmInputsBuilder()
                    .withHostname(hostname)
                    .withVirtualMachineName(virtualMachineName)
                    .build();

            resultMap = new ClusterComputeResourceService().updateOrAddVmOverride(httpInputs, vmInputs, clusterName,
                    validateRestartPriority(restartPriority));
        } catch (Exception ex) {
            resultMap = new HashMap<>();
            resultMap.put(Outputs.RETURN_CODE, Outputs.RETURN_CODE_FAILURE);
            resultMap.put(Outputs.RETURN_RESULT, ex.getMessage());
            resultMap.put(Outputs.EXCEPTION, ExceptionUtils.getStackTrace(ex));
        }
        return resultMap;
    }

    private String validateRestartPriority(String restartPriority) {
        List<String> restartPriorities = Arrays.asList(CLUSTER_RESTART_PRIORITY, DISABLED, HIGH, MEDIUM, LOW);
        for (String str : restartPriorities) {
            if (str.equalsIgnoreCase(restartPriority)) {
                return restartPriority;
            }
        }
        throw new RuntimeException(INVALID_RESTART_PRIORITY_MSG + StringUtils.join(restartPriorities, SEPARATOR));
    }
}
