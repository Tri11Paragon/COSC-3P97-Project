#include <stdlib.h>
#include <stdbool.h>
#include <android/looper.h>
#include <android/sensor.h>

ASensorManager* manager; ALooper* loop; ASensorEventQueue* queue;

void enable_sensor() {
	manager = ASensorManager_getInstanceForPackage("your.package.name");
	loop = ALooper_prepare(0);
	queue = ASensorManager_createEventQueue(manager, loop, ALOOPER_POLL_CALLBACK, sensorUpdate, NULL);

	ASensor* accel = ASensorManager_getDefaultSensor(manager, ASENSOR_TYPE_ACCELEROMETER);
	ASensorEventQueue_setEventRate(queue, accel, max(ASensor_getMinDelay(accel), 500));  // 500 us min
	ASensorEventQueue_enableSensor(queue, accel);
}

int sensorUpdate(int fd, int nevents, void *data) {
	ASensorEvent events[nevents];
	ASensorEventQueue_getEvents(queue, events, nevents)
	for (int i = 0; i < nevents; i++) {
		switch (events[i].type) {
			case ASENSOR_TYPE_ACCELEROMETER:
			// do something with events[i].acceleration.x, events[i].acceleration.y, events[i].acceleration.z
		}
	}
}

void disable_sensor() {
	ASensor* accel = ASensorManager_getDefaultSensor(manager, ASENSOR_TYPE_ACCELEROMETER);
	ASensorEventQueue_disableSensor(queue, accel);
	ASensorManager_destroyEventQueue(manager, queue);
}
